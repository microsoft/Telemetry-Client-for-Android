package com.microsoft.cll.android;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import Microsoft.Android.LoggingLibrary.Snapshot;

/**
 * This class handles uploading telemetry information about the cll itself on a timer
 */
public class SnapshotScheduler extends ScheduledWorker {
    private final String TAG = "SnapshotScheduler";
    private final ICll cll;
    private final ClientTelemetry clientTelemetry;
    private final ILogger logger;

    public SnapshotScheduler(ClientTelemetry clientTelemetry, ILogger logger, ICll cll) {
        super(SettingsStore.getCllSettingsAsLong(SettingsStore.Settings.SNAPSHOTSCHEDULEINTERVAL));
        this.cll = cll;
        this.clientTelemetry = clientTelemetry;
        this.logger = logger;
    }

    /**
     * The normal start runs the executor immediately, for this we want to wait the interval time before our first run
     */
    @Override
    public void start(ScheduledExecutorService executor) {
        this.executor = executor;
        nextExecution = executor.scheduleAtFixedRate(this, interval, interval, TimeUnit.SECONDS);
    }

    @Override
    public void resume(ScheduledExecutorService executor) {
        this.executor = executor;
        nextExecution = executor.scheduleAtFixedRate(this, interval, interval, TimeUnit.SECONDS);
        this.isPaused = false;
    }

    @Override
    public void run() {
        logger.info(TAG, "Uploading snapshot");

        // Check to see if the interval at which we should drain has changed
        if(interval != SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.SNAPSHOTSCHEDULEINTERVAL)) {
            nextExecution.cancel(false);
            interval = SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.SNAPSHOTSCHEDULEINTERVAL);
            nextExecution = executor.scheduleAtFixedRate(this, interval, interval, TimeUnit.SECONDS);
        }

        recordStatistics();
    }

    private void recordStatistics()
    {
        Snapshot snapshot = clientTelemetry.GetEvent();
        cll.log(snapshot);

        // We wait till after log to reset because if we reset before log then the event would have
        // it's attributes reset as well since we aren't doing a deep copy.
        clientTelemetry.Reset();
    }
}
