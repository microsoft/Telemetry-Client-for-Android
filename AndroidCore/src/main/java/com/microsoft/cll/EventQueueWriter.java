package com.microsoft.cll;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
    protected static int power = 1;

    private final String TAG = "EventQueueWriter";
    private final List<IStorage> storages;
    private final List<ICllEvents> cllEvents;
    private final EventBatcher batcher;
    private final SerializedEvent event;
    private final ILogger logger;
    private final ClientTelemetry clientTelemetry;
    private final ScheduledExecutorService executorService;
    private EventSender sender;
    private List<IStorage> removedStorages;
    private EventCompressor compressor;
    private EventHandler handler;
    private URL endpoint;

    /**
     * Constructor for a queue of events
     */
    public EventQueueWriter(URL endpoint, List<IStorage> storages, ClientTelemetry clientTelemetry, List<ICllEvents> cllEvents, ILogger logger, ScheduledExecutorService executorService) {
        this.cllEvents      = cllEvents;
        this.storages       = storages;
        this.logger         = logger;
        this.batcher        = new EventBatcher();
        this.sender         = new EventSender(endpoint, clientTelemetry, logger);
        this.compressor     = new EventCompressor(logger);
        this.event          = null;
        this.executorService= executorService;
        this.clientTelemetry= clientTelemetry;
        this.endpoint       = endpoint;
        this.removedStorages= new ArrayList<IStorage>();
    }

    /**
     * Constructor for a real time event
     */
    public EventQueueWriter(URL endpoint, SerializedEvent event, ClientTelemetry clientTelemetry, List<ICllEvents> cllEvents, ILogger logger, ScheduledExecutorService executorService, EventHandler handler) {
        this.cllEvents      = cllEvents;
        this.event          = event;
        this.logger         = logger;
        this.sender         = new EventSender(endpoint, clientTelemetry, logger);
        this.batcher        = null;
        this.storages       = null;
        this.executorService= executorService;
        this.clientTelemetry= clientTelemetry;
        this.handler        = handler;
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
        // This must occur before we check for running, otherwise if a normal send is running
        // we might drop this event.
        if(storages == null) {
            sendRealTimeEvent(event);
            return;
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
    protected void sendRealTimeEvent(SerializedEvent singleEvent) {
        // Check to see if this single serialized event is greater than MAX_BUFFER_SIZE, if it is we drop it.
        if (singleEvent.getSerializedData().length() > SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXEVENTSIZEINBYTES)) {
            return;
        }

        try {
            sender.sendEvent(singleEvent.getSerializedData());
        } catch (IOException e) {
            // Edge case for real time events that try to send but don't have network.
            // In this case we need to write to disk
            // Force Normal latency so we don't keep looping back to here
            logger.error(TAG, "Cannot send event");
            handler.addToStorage(singleEvent);
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
                    } else {
                        // Stop retry logic on successful send
                        future = null;
                        power = 1;
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
            } else {
                // Stop retry logic on successful send
                future = null;
                power = 1;
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

            int interval = generateBackoffInterval();

            // If we don't remove these then on next call the drain method will end up creating a new empty file by this name.
            storages.removeAll(removedStorages);

            EventQueueWriter eventQueueWriter = new EventQueueWriter(endpoint, storages, clientTelemetry, cllEvents, logger, executorService);
            eventQueueWriter.setSender(sender);
            future = executorService.schedule(eventQueueWriter, interval, TimeUnit.SECONDS);
            return false; // If we run into an error sending events we just return. This ensures we don't lose events
        }

        return true;
    }

    /**
     * Generates a random backoff interval using k*b^p.
     * k is a constant we multiply by
     * b is the base which we raise to a power
     * p is the power we raise to. where p is between 0..n where n increases everytime we fail unless increaseing it would put us over the maxretryperiod.
     * @return A retry interval
     */
    int generateBackoffInterval() {
        Random random = new Random();
        int interval = (int) (SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.CONSTANTFORRETRYPERIOD)
                * Math.pow(SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.BASERETRYPERIOD), random.nextInt(power)));

        // Increment the power if it won't put us over the max retry interval
        if(SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.CONSTANTFORRETRYPERIOD)
                * Math.pow(SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.BASERETRYPERIOD), power) <= SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXRETRYPERIOD)) {
            power++;
        }
        return interval;
    }
}