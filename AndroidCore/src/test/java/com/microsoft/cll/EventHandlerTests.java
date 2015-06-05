package com.microsoft.cll;

import com.microsoft.cll.Helpers.EventHelper;
import com.microsoft.cll.Helpers.PartAHelper;
import com.microsoft.cll.Overrides.CriticalEventHandlerOverride;
import com.microsoft.cll.Overrides.NormalEventHandlerOverride;
import com.microsoft.telemetry.Envelope;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public class EventHandlerTests {
    private URL url;
    private String filePath;

    @Before
    public void setUp() {
        try {
            url = new URL("http://www.test.com");

            filePath = new File(".").getCanonicalPath() + File.separator + "cllEvents";
            File dir = new File(filePath);
            if(!dir.exists()) {
                dir.mkdir();
            }
        } catch (Exception e) {
            cleanup();
        }
    }

    @After
    public void cleanup(){
    }

    @Test
    public void testDeviceInSampleGroup() {
        CriticalEventHandlerOverride criticalEventHandlerOverride = new CriticalEventHandlerOverride(new CustomLogger(), filePath);
        NormalEventHandlerOverride normalEventHandlerOverride = new NormalEventHandlerOverride(new CustomLogger(), filePath);
        EventHandler eventHandler = new EventHandler(new ClientTelemetry("test"), new ArrayList<ICllEvents>(), new CustomLogger(), normalEventHandlerOverride, criticalEventHandlerOverride);
        Envelope event = (Envelope)EventHelper.generateABCEvent();
        PartAHelper.setDeviceId(event, "0000000000"); // Sample Group 0
        eventHandler.log(event, Cll.EventPersistence.NORMAL, Cll.EventLatency.NORMAL);
        assert(normalEventHandlerOverride.eventCount == 1);
    }

    @Test
    public void testDeviceNotInSampleGroup() {
        // Create new event handler to reset the deviceId since we store it in event handler after first call
        CriticalEventHandlerOverride criticalEventHandlerOverride = new CriticalEventHandlerOverride(new CustomLogger(), filePath);
        NormalEventHandlerOverride normalEventHandlerOverride = new NormalEventHandlerOverride(new CustomLogger(), filePath);
        EventHandler eventHandler = new EventHandler(new ClientTelemetry("test"), new ArrayList<ICllEvents>(), new CustomLogger(), normalEventHandlerOverride, criticalEventHandlerOverride);
        Envelope event = (Envelope)EventHelper.generateABCEvent();
        PartAHelper.setDeviceId(event, "000000099"); // Sample Group 53
        eventHandler.log(event, Cll.EventPersistence.NORMAL, Cll.EventLatency.NORMAL);
        assert(normalEventHandlerOverride.eventCount == 0);
    }

    @Test
    public void testAddToStorage() {
        CriticalEventHandlerOverride criticalEventHandlerOverride = new CriticalEventHandlerOverride(new CustomLogger(), filePath);
        NormalEventHandlerOverride normalEventHandlerOverride = new NormalEventHandlerOverride(new CustomLogger(), filePath);
        EventHandler eventHandler = new EventHandler(new ClientTelemetry("test"), new ArrayList<ICllEvents>(), new CustomLogger(), normalEventHandlerOverride, criticalEventHandlerOverride);
        Envelope event = (Envelope)EventHelper.generateABCEvent();
        PartAHelper.setDeviceId(event, "0000000000"); // Sample Group 0

        for(int i = 0; i < 10; i++) {
            eventHandler.log(event, Cll.EventPersistence.CRITICAL, Cll.EventLatency.NORMAL);
        }

        for(int i = 0; i < 10; i++) {
            eventHandler.log(event, Cll.EventPersistence.NORMAL, Cll.EventLatency.NORMAL);
        }

        assert(normalEventHandlerOverride.eventCount == 10);
        assert(criticalEventHandlerOverride.eventCount == 10);
    }

    @Test
    public void testNullDeviceId() {
        CriticalEventHandlerOverride criticalEventHandlerOverride = new CriticalEventHandlerOverride(new CustomLogger(), filePath);
        NormalEventHandlerOverride normalEventHandlerOverride = new NormalEventHandlerOverride(new CustomLogger(), filePath);
        EventHandler eventHandler = new EventHandler(new ClientTelemetry("test"), new ArrayList<ICllEvents>(), new CustomLogger(), normalEventHandlerOverride, criticalEventHandlerOverride);
        Envelope event = (Envelope)EventHelper.generateABCEvent();
        PartAHelper.setDeviceId(event, null); // Sample Group null
        eventHandler.log(event, Cll.EventPersistence.NORMAL, Cll.EventLatency.NORMAL);
        assert(normalEventHandlerOverride.eventCount == 0);
    }
}
