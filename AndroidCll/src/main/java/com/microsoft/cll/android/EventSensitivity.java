package com.microsoft.cll.android;

/**
 * Created by jmorman on 8/31/2015.
 */
public enum EventSensitivity {
    None(0x0),
    Mark(0x80000),
    Hash(0x100000),
    Drop(0x200000);

    private int value;

    private EventSensitivity(int v) {
        value = v;
    }

    public int getCode() {
        return value;
    }
}
