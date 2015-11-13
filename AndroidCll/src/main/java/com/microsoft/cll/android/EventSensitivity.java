package com.microsoft.cll.android;

public enum EventSensitivity {
    None(0x000000),
    Mark(0x080000),
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
