package com.microsoft.cll;

import com.microsoft.telemetry.Envelope;
import com.microsoft.telemetry.IJsonSerializable;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class takes care of getting queued events ready to send
 */
public class EventQueueWriter implements Runnable {
    protected static AtomicBoolean running = new AtomicBoolean(false);
    protected static ScheduledFuture future;

    private final String TAG = "EventQueueWriter";
    private final List<IStorage> storages;
    private final List<ICllEvents> cllEvents;
    private final EventSerializer serializer;
    private final EventBatcher batcher;
    private final IJsonSerializable event;
    private final ILogger logger;
    private final ClientTelemetry clientTelemetry;
    private final ScheduledExecutorService executorService;
    private EventSender sender;
    private List<IStorage> removedStorages;
    private EventCompressor compressor;
    private EventHandler handler;
    private int period;
    private URL endpoint;

    /**
     * Constructor for a queue of events
     */
    public EventQueueWriter(URL endpoint, List<IStorage> storages, ClientTelemetry clientTelemetry, List<ICllEvents> cllEvents, ILogger logger, ScheduledExecutorService executorService, int period) {
        this.cllEvents      = cllEvents;
        this.storages       = storages;
        this.logger         = logger;
        this.batcher        = new EventBatcher();
        this.sender         = new EventSender(endpoint, clientTelemetry, logger);
        this.serializer     = new EventSerializer(logger);
        this.compressor     = new EventCompressor(logger);
        this.event          = null;
        this.executorService= executorService;
        this.clientTelemetry= clientTelemetry;
        this.period         = period;
        this.endpoint       = endpoint;
        this.removedStorages= new ArrayList<IStorage>();
    }

    /**
     * Constructor for a real time event
     */
    public EventQueueWriter(URL endpoint, IJsonSerializable event, ClientTelemetry clientTelemetry, List<ICllEvents> cllEvents, ILogger logger, ScheduledExecutorService executorService, EventHandler handler, int period) {
        this.cllEvents      = cllEvents;
        this.event          = event;
        this.logger         = logger;
        this.sender         = new EventSender(endpoint, clientTelemetry, logger);
        this.serializer     = new EventSerializer(logger);
        this.batcher        = null;
        this.storages       = null;
        this.executorService= executorService;
        this.clientTelemetry= clientTelemetry;
        this.handler        = handler;
        this.period         = period;
        this.endpoint       = endpoint;

        clientTelemetry.IncrementEventsQueuedForUpload();
    }

    void setSender(EventSender sender) {
        this.sender = sender;
    }

    @Override
    public void run() {
        logger.info(TAG, "Starting upload");

        // Send real time event
        if(storages == null) {
            sendRealTimeEvent(event);
            return;
        }

        // If the next (normal periodic) scheduled drain is starting and we have a back-off retry scheduled future set, cancel it.
        if(this.period == 1 && future != null) {
            logger.info(TAG, "Canceling future sender");
            future.cancel(false);
        }

        // Send events with normal persistence
        if(!running.compareAndSet(false, true)) {
            logger.info(TAG, "Skipping send, event sending is already in progress on different thread.");
            return;
        }

        send();
        running.set(false);

    }

    /**
     * Sends a real time event by itself
     */
    protected void sendRealTimeEvent(IJsonSerializable singleEvent) {
        String serialized = serializer.serialize(singleEvent);

        // Check to see if this single serialized event is greater than MAX_BUFFER_SIZE, if it is we drop it.
        if (serialized.length() > SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXEVENTSIZEINBYTES)) {
            return;
        }

        try {
            sender.sendEvent(serialized);
        } catch (IOException e) {
            // Edge case for real time events that try to send but don't have network.
            // In this case we need to write to disk
            // Force Normal latency so we don't keep looping back to here
            PartAFlags tags = new PartAFlags((Envelope)singleEvent);
            handler.log(singleEvent, tags.getPersistence(), Cll.EventLatency.NORMAL);
            logger.error(TAG, "Cannot send event");
        }

        for(ICllEvents event : cllEvents) {
            event.sendComplete();
        }
    }

    /**
     * Serializes, batches, and sends events
     */
    protected void send() {
        // Ensure that the serialized event string is under MAXEVENTSIZEINBYTES.
        // If it is over MAXEVENTSIZEINBYTES then we should use 2 or more strings and send them
        for(IStorage storage : storages) {
            if(executorService.isShutdown()) {
                return;
            }

            List<String> events = storage.drain();
            for (String event : events) {
                this.clientTelemetry.IncrementEventsQueuedForUpload();

                // Check to see if this single serialized event is greater than MAX_BUFFER_SIZE, if it is we drop it.
                if (event.length() > SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXEVENTSIZEINBYTES)) {

                    // This could cause big problems if the host application decides to do a ton of processing for each
                    // dropped event.
                    for(ICllEvents cllEvent : cllEvents) {
                        cllEvent.eventDropped(event);
                    }

                    continue;
                }

                if (batcher.canAddToBatch(event)) {
                    try {
                        batcher.addEventToBatch(event);
                    } catch (EventBatcher.BatchFullException e) {
                        logger.error(TAG, "Could not add to batch");
                    }
                } else {
                    // Full batch, send events
                    String batchedEvents = batcher.getBatchedEvents();

                    try {
                        batcher.addEventToBatch(event);
                    } catch (EventBatcher.BatchFullException e) {
                        logger.error(TAG, "Could not add to batch");
                    }

                    boolean sendResult = sendBatch(batchedEvents, storage);
                    if(sendResult == false)
                    {
                        storage.close();
                        return;
                    }
                }
            }

            // Send remaining events that didn't fill a whole batch
            String batchedEvents = batcher.getBatchedEvents();
            boolean sendResult = sendBatch(batchedEvents, storage);
            if(sendResult == false)
            {
                storage.close();
                return;
            }

            storage.discard();
        }

        logger.info(TAG, "Sent " + clientTelemetry.snapshot.getEventsQueuedForUpload() + " events.");

        for(ICllEvents event : cllEvents) {
            event.sendComplete();
        }
    }

    private boolean sendBatch(String batchedEvents, IStorage storage) {
        // This is "" if we upload an empty file which we should just skip
        if(batchedEvents.equals("")) {
            removedStorages.add(storage);
            return true;
        }

        byte[] compressedBatchedEvents = compressor.compress(batchedEvents);

        // Write event string
        try {
            if(compressedBatchedEvents != null) {
                sender.sendEvent(compressedBatchedEvents, true);
            } else {
                sender.sendEvent(batchedEvents);
            }
        } catch (IOException e) {
            logger.error(TAG, "Cannot send event: " + e.getMessage());
            int newPeriod = period*2;
            if(newPeriod > SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXRETRYPERIOD)) {
                // The next scheduled drain is coming soon (~2.5 min so going higher exponentially won't help)
                return false;
            }

            // If we don't remove these then on next call the drain method will end up creating a new empty file by this name.
            storages.removeAll(removedStorages);

            EventQueueWriter r = new EventQueueWriter(endpoint, storages, clientTelemetry, cllEvents, logger, executorService, newPeriod);
            r.setSender(sender);
            future = executorService.schedule(r, newPeriod, TimeUnit.SECONDS);
            return false; // If we run into an error sending events we just return. This ensures we don't lose events
        }

        return true;
    }
}