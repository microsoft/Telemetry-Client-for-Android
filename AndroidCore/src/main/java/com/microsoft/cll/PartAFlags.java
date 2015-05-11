package com.microsoft.cll;

import com.microsoft.telemetry.Envelope;

/**
 * Some helper methods for dealing with flags in part A
 */
public class PartAFlags {
    Envelope event;

    public PartAFlags(Envelope event) {
        this.event = event;
    }

    public Cll.EventPersistence getPersistence() {
        int persistence = (int) event.getFlags() & 0x0F;
        return Cll.EventPersistence.getPersistence(persistence);
    }

    public Cll.EventLatency getLatency() {
        int latency = ((int) event.getFlags() & 0xF0) >> 4;
        return Cll.EventLatency.getLatency(latency);
    }
}
