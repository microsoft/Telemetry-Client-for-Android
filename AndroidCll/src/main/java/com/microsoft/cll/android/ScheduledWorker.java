package com.microsoft.cll.android;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * This is a base class for setting up a scheduled task
 */
public abstract class ScheduledWorker implements Runnable {

    protected ScheduledFuture nextExecution;
    protected ScheduledExecutorService executor;
    protected long interval;
    protected boolean isPaused;


    public ScheduledWorker(long interval) {
        this.interval = interval;
        this.isPaused = false;
    }

    public abstract void run();

    protected void start(ScheduledExecutorService executor) {
        setupExecutor(executor);
    }

    protected void stop() {
        // Cancels the current sync even if in progress.
        nextExecution.cancel(true);
    }

    protected void pause() {
        // Cancels the current sync if it has not yet started, otherwise gracefully shuts down after sync finishes
        nextExecution.cancel(false);
        this.isPaused = true;
    }

    protected void resume(ScheduledExecutorService executor) {
        setupExecutor(executor);
        this.isPaused = false;
    }

    private void setupExecutor(ScheduledExecutorService executor) {
        this.executor = executor;
        nextExecution = executor.scheduleAtFixedRate(this, 0, interval, TimeUnit.SECONDS);
    }
}
