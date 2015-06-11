package com.microsoft.cll.Overrides;

import com.microsoft.cll.CriticalEventHandler;
import com.microsoft.cll.ILogger;

public class CriticalEventHandlerOverride extends CriticalEventHandler {
    public int eventCount;

    public CriticalEventHandlerOverride(ILogger logger, String filePath) {
        super(logger, filePath);
    }

    @Override
    public synchronized void add(String event) {
        eventCount++;
    }
}
