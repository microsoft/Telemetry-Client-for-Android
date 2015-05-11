package com.microsoft.cll.Helpers;

import com.microsoft.cll.IStorage;
import com.microsoft.telemetry.IJsonSerializable;

import java.util.ArrayList;
import java.util.List;

public class CustomStorageHelper implements IStorage {
    public int eventCount;

    @Override
    public void add(IJsonSerializable event) {
        eventCount++;
    }

    @Override
    public void add(String event) throws Exception {
        eventCount++;
    }

    @Override
    public boolean canAdd(IJsonSerializable event) {
        return true;
    }

    @Override
    public boolean canAdd(String event) {
        return true;
    }

    @Override
    public List<String> drain() {
        ArrayList<String> events = new ArrayList<String>(eventCount);
        for(int i = 0; i < eventCount; i++) {
            events.add(EventHelper.singleGoodJsonEvent);
        }

        return events;
    }

    @Override
    public long size() {
        return eventCount;
    }

    @Override
    public void discard() {
        eventCount = 0;
    }

    @Override
    public void close() {

    }


}
