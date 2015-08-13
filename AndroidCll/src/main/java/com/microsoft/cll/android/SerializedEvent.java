package com.microsoft.cll.android;

/**
 * This class provides a layer that abstracts out which kind of Envelope we are using by only holding references
 * to the important pieces and serializing the data immediately.
 */
public class SerializedEvent {
    private String serializedData;
    private Cll.EventLatency latency;
    private Cll.EventPersistence persistence;
    private double sampleRate;
    private String deviceId;

    public String getSerializedData() {
        return serializedData;
    }

    public void setSerializedData(String serializedData) {
        this.serializedData = serializedData;
    }

    public Cll.EventLatency getLatency() {
        return latency;
    }

    public void setLatency(Cll.EventLatency latency) {
        this.latency = latency;
    }

    public Cll.EventPersistence getPersistence() {
        return persistence;
    }

    public void setPersistence(Cll.EventPersistence persistence) {
        this.persistence = persistence;
    }

    public double getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(double sampleRate) {
        this.sampleRate = sampleRate;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
