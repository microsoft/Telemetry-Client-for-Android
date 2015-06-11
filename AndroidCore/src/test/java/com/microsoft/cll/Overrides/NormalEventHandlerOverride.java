package com.microsoft.cll.Overrides;

import com.microsoft.cll.ILogger;
import com.microsoft.cll.IStorage;
import com.microsoft.cll.NormalEventHandler;

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
