/**
 * Copyright Microsoft Corporation 2014
 * All Rights Reserved
 */

package com.microsoft.cll;

import com.microsoft.telemetry.Base;
import com.microsoft.telemetry.IChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The cll main class that should be called into via the client application.
 * The CLL must be initialized first with the <code>start()</code> method, which
 * starts the thread for collection of <code>IJsonSerializable</code> events, and then
 * <code>setEndpointUrl</code> must be called to set the url for the events to be sent to.
 */
public class Cll implements IChannel
{
    public CorrelationVector correlationVector;

    protected final ClientTelemetry clientTelemetry;
    protected final String TAG = "Cll";
    protected final List<ICllEvents> cllEvents;

    protected EventHandler eventHandler;
    protected ILogger logger;
    protected PartA partA;
    protected SettingsSync settingsSync;
    protected SnapshotScheduler snapshotScheduler;

    private final AtomicBoolean isChanging;
    private final AtomicBoolean isPaused;
    private final AtomicBoolean isStarted;

    private ScheduledExecutorService executor;

    /**
     * Initializes the CLL with the given provider.
     */
    protected Cll(String iKey, ILogger logger, String cllName)
    {
        this.logger              = logger;
        this.clientTelemetry     = new ClientTelemetry(cllName);
        this.cllEvents           = new ArrayList<ICllEvents>();
        this.correlationVector   = new CorrelationVector();
        this.isChanging          = new AtomicBoolean(false);
        this.isStarted           = new AtomicBoolean(false);
        this.isPaused            = new AtomicBoolean(false);
        this.settingsSync        = new SettingsSync(clientTelemetry, logger, iKey);
        this.snapshotScheduler   = new SnapshotScheduler(clientTelemetry, logger, this);

        this.logger.setVerbosity(ILogger.Verbosity.INFO);
    }

    /**
     * Starts the queue-draining background thread and uploader. Start must be called prior
     * to logging events in order to start the queue-draining background thread
     * and uploader.
     */
    public void start()
    {
        if(this.isChanging.compareAndSet(false, true)) {
            if(!this.isStarted.get()) {
                this.executor = Executors.newScheduledThreadPool(3);
                this.snapshotScheduler.start(this.executor);
                this.eventHandler.start(this.executor);
                this.settingsSync.start(this.executor);

                this.isStarted.set(true);
            }

            this.isChanging.set(false);
        }
    }

    /**
     * Stops the background thread and uploader.
     */
    public void stop()
    {
        // Ensure we are actually started and then if we are attempt to shut is down.
        // NOTE: right now re are not guaranteeing that when we return we are actually stopped.
        if (this.isChanging.compareAndSet(false, true))
        {
            if(this.isStarted.get()) {
                this.eventHandler.stop();
                this.settingsSync.stop();
                this.snapshotScheduler.stop();
                this.executor.shutdown();

                this.isStarted.set(false);
            }

            for(ICllEvents event : this.cllEvents) {
                event.stopped();
            }

            this.isChanging.set(false);
        }
    }

    /**
     * Puts the Cll in a paused state, allowing it to accept events but not
     * upload until it is resumed.
     */
    public void pause()
    {
        // Ensure we are actually started and then if we are attempt to shut is down.
        // NOTE: right now re are not guaranteeing that when we return we are actually stopped.
        if (this.isChanging.compareAndSet(false, true)) {
            if(this.isStarted.get() && !this.isPaused.get()) {
                this.eventHandler.pause();
                this.settingsSync.pause();
                this.snapshotScheduler.pause();
                this.executor.shutdown();

                this.isPaused.set(true);
            }

            this.isChanging.set(false);
        }
    }

    /**
     * Resume the Cll from a paused state, allowing uploads to resume.
     * During resume an upload will automatically be triggered.
     */
    public void resume()
    {
        // Ensure we are actually started and then if we are attempt to shut is down.
        // NOTE: right now we are not guaranteeing that when we return we are actually started.
        if (this.isChanging.compareAndSet(false, true))
        {
            if(this.isStarted.get() && this.isPaused.get()) {
                // There is no way to restart an executor, we must create a new one instead
                this.executor = Executors.newScheduledThreadPool(SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.THREADSTOUSEWITHEXECUTOR));
                this.snapshotScheduler.resume(this.executor);
                this.eventHandler.resume(this.executor);
                this.settingsSync.resume(this.executor);

                this.isPaused.set(false);
            }

            this.isChanging.set(false);
        }
    }

    /**
     * Allow the host application to set the verbosity to help with debugging during runtime
     * @param verbosity - The verbosity to use
     */
    public void setDebugVerbosity(ILogger.Verbosity verbosity)
    {
        logger.setVerbosity(verbosity);
    }

    /**
     * Log a bond event
     *
     * @param event
     *            The serializable event to log
     */
    public void log(final PreSerializedEvent event)
    {
        PreSerializedJsonSerializable preSerializedJsonSerializable = new PreSerializedJsonSerializable(event.data, event.partCName, event.partBName, event.attributes);
        log(preSerializedJsonSerializable);
    }

    /**
     * Log a bond event
     * 
     * @param event
     *            The serializable event to log
     */
    public void log(final Base event)
    {
        log(event, null);
    }

    public void log(final Base event, Map<String, String> tags) {
        if (!this.isStarted.get())
        {
            this.logger.error(TAG, "Cll must be started before logging events");
            return;
        }

        final SerializedEvent serializedEvent = this.partA.populate(event, this.correlationVector.GetValue(), tags);
        this.eventHandler.log(serializedEvent);
    }

    /**
     * Uploads all events in the queue
     */
    public void send()
    {
        if(this.isStarted.get()) {
            this.eventHandler.send();
        } else {
            logger.info(TAG, "Cannot send while the CLL is stopped.");
        }
    }

    /**
     * Sets the URL used to send events to
     * setEndpointUrl must be called before events can be sent
     *
     * @param url
     *            Url, including protocol and port
     */
    public void setEndpointUrl(final String url)
    {
        this.eventHandler.setEndpointUrl(url);
    }

    /**
     * Set's whether we should use the legacy part A fields or not.
     * @param value True if we should, false if we should not
     */
    public void useLagacyCS(boolean value) {
        partA.useLagacyCS(value);
    }

    /**
     * Sets the experiment id
     * @param id
     *           The experiment id
     */
    public void setExperimentId(String id) {
        partA.setExpId(id);
    }

    public void synchronize() {
        eventHandler.synchronize();
    }

    /**
     * Used for testing so we can inject a custom EventSender to test events
     * @param sender
     */
    protected void setEventSender(EventSender sender)
    {
        eventHandler.setSender(sender);
    }

    /**
     * enum for the persistence of an event
     */
    public enum EventPersistence
    {
        NORMAL(1),
        CRITICAL(2);

        private int value;

        private EventPersistence(int v) {
            value = v;
        }

        public int getCode() {
            return value;
        }

        public static EventPersistence getPersistence(int value) {
            switch (value) {
                case 1:
                    return NORMAL;
                case 2:
                    return CRITICAL;
            }

            return null;
        }
    }

    /**
     * Enum for the latency of an event
     */
    public enum EventLatency
    {
        NORMAL(1),
        REALTIME(2);

        private int value;

        private EventLatency(int v) {
            value = v;
        }

        public int getCode() {
            return value;
        }

        public static EventLatency getLatency(int value) {
            switch (value) {
                case 1:
                    return NORMAL;
                case 2:
                    return REALTIME;
            }

            return null;
        }
    }
}
