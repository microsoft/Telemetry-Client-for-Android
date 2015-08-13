package com.microsoft.cll.android.Overrides;

import com.microsoft.cll.android.ILogger;
import com.microsoft.cll.android.IStorage;
import com.microsoft.cll.android.NormalEventHandler;

public class NormalEventHandlerOverride extends NormalEventHandler {
    public int eventCount;

    public NormalEventHandlerOverride(ILogger logger, String filePath) {
        super(logger, filePath);
    }

    @Override
    public synchronized void add(String event) {
        eventCount++;
    }

    @Override
    public void dispose(IStorage storage) {
        
    }
}
