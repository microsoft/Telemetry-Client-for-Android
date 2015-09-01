package com.microsoft.cll.android;

/**
 * Created by jmorman on 8/31/2015.
 */
public enum EventSensitivity {
    Mark(8),
    Hash(1),
    Drop(2);

    private int value;

    private EventSensitivity(int v) {
        value = v;
    }

    public int getCode() {
        return value;
    }
}
