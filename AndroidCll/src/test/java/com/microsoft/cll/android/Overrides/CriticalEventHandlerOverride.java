package com.microsoft.cll.android.Overrides;

import com.microsoft.cll.android.ClientTelemetry;
import com.microsoft.cll.android.CriticalEventHandler;
import com.microsoft.cll.android.ILogger;

public class CriticalEventHandlerOverride extends CriticalEventHandler {
    public int eventCount;

    public CriticalEventHandlerOverride(ILogger logger, String filePath) {
        super(logger, filePath, new ClientTelemetry());
    }

    @Override
    public synchronized void add(String event) {
        eventCount++;
    }
}
