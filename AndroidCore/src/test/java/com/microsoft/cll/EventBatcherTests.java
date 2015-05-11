package com.microsoft.cll;

import com.microsoft.cll.Helpers.EventHelper;
import org.junit.Before;
import org.junit.Test;

public class EventBatcherTests {
    private EventBatcher eventBatcher;

    @Before
    public void setup() {
        eventBatcher = new EventBatcher();
    }

    @Test
    public void testAdding10EventsToBatch() {
        try {
            for (int i = 0; i < 10; i++) {
                eventBatcher.addEventToBatch(EventHelper.singleGoodJsonEvent);
            }
        } catch (Exception e) {}

        String batchedEvents = eventBatcher.getBatchedEvents();
        int eventCount = batchedEvents.split("\r\n").length;
        assert(eventCount == 10);
    }

    @Test
    public void testAddingTooManyEventsToBatch() {
        final int maxVortexPayloadSize = 64*1024; // 64 KB
        final int serializedEventSize = EventHelper.singleGoodJsonEvent.length();
        int currentSize = 0;
        int count = 0;
        boolean failedToAdd = false;
        try {
            while (currentSize + serializedEventSize < maxVortexPayloadSize) {
                eventBatcher.addEventToBatch(EventHelper.singleGoodJsonEvent);
                currentSize += serializedEventSize;
                count++;
            }

            eventBatcher.addEventToBatch(EventHelper.singleGoodJsonEvent);
            currentSize += serializedEventSize;
            count++;
        } catch (Exception e) {
            // We expect this exception
            failedToAdd = true;
        }

        // Assert that the last event we tried to add didn't actually add
        assert(failedToAdd == true);

        String batchedEvents = eventBatcher.getBatchedEvents();
        int eventCount = batchedEvents.split("\r\n").length;
        assert(count == eventCount);

        try {
            for(int i = 0; i < 10; i++) {
                eventBatcher.addEventToBatch(EventHelper.singleGoodJsonEvent);
            }
            batchedEvents = eventBatcher.getBatchedEvents();
            eventCount = batchedEvents.split("\r\n").length;
            assert(eventCount == 10);
        }catch (Exception e) {
        }
    }
}