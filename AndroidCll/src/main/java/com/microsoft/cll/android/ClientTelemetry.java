package com.microsoft.cll.android;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import Microsoft.Android.LoggingLibrary.Snapshot;
import Ms.Telemetry.CllHeartBeat;

/**
 * Captures telemetry on how the cll is being used
 */
public class ClientTelemetry {
    protected CllHeartBeat snapshot;
    private ArrayList<Integer> settingsCallLatencies;
    private ArrayList<Integer> vortexCallLatencies;

    public ClientTelemetry() {
        snapshot                = new CllHeartBeat();
        settingsCallLatencies   = new ArrayList<Integer>();
        vortexCallLatencies     = new ArrayList<Integer>();

        Reset();
    }

    protected Snapshot GetEvent() {
        Snapshot snapshotEvent = new Snapshot();
        snapshotEvent.setBaseData(snapshot);
        return snapshotEvent;
    }

    protected void Reset() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        snapshot.setLastHeartBeat(dateFormat.format(new Date()).toString());
        snapshot.setEventsQueued(0);
        snapshot.setLogFailures(0);
        snapshot.setQuotaDropCount(0);
        snapshot.setRejectDropCount(0);
        snapshot.setVortexHttpAttempts(0);
        snapshot.setVortexHttpFailures(0);
        snapshot.setCacheUsagePercent(0);
        snapshot.setAvgVortexLatencyMs(0);
        snapshot.setMaxVortexLatencyMs(0);
        snapshot.setSettingsHttpAttempts(0);
        snapshot.setSettingsHttpFailures(0);
        snapshot.setAvgSettingsLatencyMs(0);
        snapshot.setMaxSettingsLatencyMs(0);
        snapshot.setVortexFailures4xx(0);
        snapshot.setVortexFailures5xx(0);
        snapshot.setVortexFailuresTimeout(0);
        snapshot.setSettingsFailures4xx(0);
        snapshot.setSettingsFailures5xx(0);
        snapshot.setSettingsFailuresTimeout(0);

        settingsCallLatencies.clear();
        vortexCallLatencies.clear();
    }

    protected void IncrementEventsQueuedForUpload() {
        IncrementEventsQueuedForUpload(1);
    }

    protected void IncrementEventsQueuedForUpload(int count) {
        int queueCount = snapshot.getEventsQueued() + count;
        snapshot.setEventsQueued(queueCount);
    }

    protected void IncrementLogFailures() {
        int errorCount = snapshot.getLogFailures() + 1;
        snapshot.setLogFailures(errorCount);
    }

    protected void IncrementEventsDroppedDueToQuota() {
        int count = snapshot.getQuotaDropCount() + 1;
        snapshot.setQuotaDropCount(count);
    }

    protected void IncrementSettingsHttpAttempts() {
        int attempts = snapshot.getSettingsHttpAttempts() + 1;
        snapshot.setSettingsHttpAttempts(attempts);
    }

    protected void IncrementVortexHttpAttempts() {
        int failures = snapshot.getVortexHttpAttempts() + 1;
        snapshot.setVortexHttpAttempts(failures);
    }

    protected void IncrementVortexHttpFailures(int errorCode) {
        int failures = snapshot.getVortexHttpFailures() + 1;
        snapshot.setVortexHttpFailures(failures);

        if (errorCode >= 400 && errorCode < 500) {
            int fourHunredFailures = snapshot.getVortexFailures4xx() + 1;
            snapshot.setVortexFailures4xx(fourHunredFailures);
        }

        if (errorCode >= 500 && errorCode < 600) {
            int fiveHunredFailures = snapshot.getVortexFailures5xx() + 1;
            snapshot.setVortexFailures5xx(fiveHunredFailures);
        }

        if (errorCode == -1) {
            int timeoutFailures = snapshot.getVortexFailuresTimeout() + 1;
            snapshot.setVortexFailuresTimeout(timeoutFailures);
        }
    }

    protected void IncrementSettingsHttpFailures(int errorCode) {
        int failures = snapshot.getSettingsHttpFailures() + 1;
        snapshot.setSettingsHttpFailures(failures);

        if (errorCode >= 400 && errorCode < 500) {
            int fourHunredFailures = snapshot.getSettingsFailures4xx() + 1;
            snapshot.setSettingsFailures4xx(fourHunredFailures);
        }

        if (errorCode >= 500 && errorCode < 600) {
            int fiveHunredFailures = snapshot.getSettingsFailures5xx() + 1;
            snapshot.setSettingsFailures5xx(fiveHunredFailures);
        }

        if (errorCode == -1) {
            int timeoutFailures = snapshot.getSettingsFailuresTimeout() + 1;
            snapshot.setSettingsFailuresTimeout(timeoutFailures);
        }
    }

    protected void SetCacheUsagePercent(double percent) {
        snapshot.setCacheUsagePercent(percent);
    }

    protected void SetAvgSettingsLatencyMs(int time) {
        settingsCallLatencies.add(time);
        int total = 0;
        for(int i : settingsCallLatencies) {
            total += i;
        }

        int average = total / settingsCallLatencies.size();
        snapshot.setAvgSettingsLatencyMs(average);
    }

    protected void SetMaxSettingsLatencyMs(int time) {
        if(snapshot.getMaxSettingsLatencyMs() < time) {
            snapshot.setMaxSettingsLatencyMs(time);
        }
    }

    protected void SetAvgVortexLatencyMs(int time) {
        vortexCallLatencies.add(time);
        int total = 0;
        for(int i : vortexCallLatencies) {
            total += i;
        }

        int average = total / vortexCallLatencies.size();
        snapshot.setAvgVortexLatencyMs(average);
    }

    protected void SetMaxVortexLatencyMs(int time) {
        if(snapshot.getMaxVortexLatencyMs() < time) {
            snapshot.setMaxVortexLatencyMs(time);
        }
    }

    protected void IncremenetRejectDropCount(int numberOfEventsDropped) {
        int dropped = snapshot.getRejectDropCount() + numberOfEventsDropped;
        snapshot.setRejectDropCount(dropped);
    }
}