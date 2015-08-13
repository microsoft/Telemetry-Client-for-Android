package com.microsoft.cll.android;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import Microsoft.Android.LoggingLibrary.Snapshot;
import Ms.Telemetry.ClientSnapshot;

/**
 * Captures telemetry on how the cll is being used
 */
public class ClientTelemetry {
    protected ClientSnapshot snapshot;
    private ArrayList<Integer> settingsCallLatencies;
    private ArrayList<Integer> vortexCallLatencies;
    private String cllName;

    public ClientTelemetry(String cllName) {
        this.cllName = cllName;

        snapshot                = new ClientSnapshot();
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
        snapshot.setUploader(cllName);
        snapshot.setUploaderVersion(BuildNumber.BuildNumber);
        snapshot.setLastSnapshotTimeStamp(dateFormat.format(new Date()).toString());
        snapshot.setEventsQueuedForUpload(0);
        snapshot.setRuntimeErrors(0);
        snapshot.setEventsDroppedDueToQuota(0);
        snapshot.setVortexHttpAttempts(0);
        snapshot.setVortexHttpFailures(0);
        snapshot.setCacheUsagePercent(0);
        snapshot.setAvgVortexResponseLatencyMs(0);
        snapshot.setMaxVortexResponseLatencyMs(0);
        snapshot.setSettingsHttpAttempts(0);
        snapshot.setSettingsHttpFailures(0);
        snapshot.setAvgSettingsResponseLatencyMs(0);
        snapshot.setMaxSettingsResponseLatencyMs(0);
        settingsCallLatencies.clear();
        vortexCallLatencies.clear();
    }

    protected void IncrementEventsQueuedForUpload() {
        IncrementEventsQueuedForUpload(1);
    }

    protected void IncrementEventsQueuedForUpload(int count) {
        int queueCount = snapshot.getEventsQueuedForUpload() + count;
        snapshot.setEventsQueuedForUpload(queueCount);
    }

    protected void IncrementRunTimeErrors() {
        int errorCount = snapshot.getRuntimeErrors() + 1;
        snapshot.setRuntimeErrors(errorCount);
    }

    protected void IncrementEventsDroppedDueToQuota() {
        int count = snapshot.getEventsDroppedDueToQuota() + 1;
        snapshot.setEventsDroppedDueToQuota(count);
    }

    protected void IncrementSettingsHttpAttempts() {
        int attempts = snapshot.getSettingsHttpAttempts() + 1;
        snapshot.setSettingsHttpAttempts(attempts);
    }

    protected void IncrementSettingsHttpFailures() {
        int failures = snapshot.getSettingsHttpFailures() + 1;
        snapshot.setSettingsHttpFailures(failures);
    }

    protected void IncrementVortexHttpAttempts() {
        int failures = snapshot.getVortexHttpAttempts() + 1;
        snapshot.setVortexHttpAttempts(failures);
    }

    protected void IncrementVortexHttpFailures() {
        int failures = snapshot.getVortexHttpFailures() + 1;
        snapshot.setVortexHttpFailures(failures);
    }

    protected void SetCacheUsagePercent(double percent) {
        snapshot.setCacheUsagePercent(percent);
    }

    protected void SetAvgSettingsResponseLatencyMs(int time) {
        settingsCallLatencies.add(time);
        int total = 0;
        for(int i : settingsCallLatencies) {
            total += i;
        }

        int average = total / settingsCallLatencies.size();
        snapshot.setAvgSettingsResponseLatencyMs(average);
    }

    protected void SetMaxSettingsResponseLatencyMs(int time) {
        if(snapshot.getMaxSettingsResponseLatencyMs() < time) {
            snapshot.setMaxSettingsResponseLatencyMs(time);
        }
    }

    protected void SetAvgVortexResponseLatencyMs(int time) {
        vortexCallLatencies.add(time);
        int total = 0;
        for(int i : vortexCallLatencies) {
            total += i;
        }

        int average = total / vortexCallLatencies.size();
        snapshot.setAvgVortexResponseLatencyMs(average);
    }

    protected void SetMaxVortexResponseLatencyMs(int time) {
        if(snapshot.getMaxVortexResponseLatencyMs() < time) {
            snapshot.setMaxVortexResponseLatencyMs(time);
        }
    }
}
