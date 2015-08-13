package com.microsoft.cll.android;

import com.microsoft.cll.android.Helpers.EventHelper;
import com.microsoft.cll.android.Overrides.CriticalEventHandlerOverride;
import com.microsoft.cll.android.Overrides.NormalEventHandlerOverride;

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
        SerializedEvent event = new SerializedEvent();
        event.setSerializedData(EventHelper.singleGoodJsonEvent);
        event.setDeviceId("0000000000");  // Sample Group 0
        event.setSampleRate(SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.SAMPLERATE));
        event.setPersistence(Cll.EventPersistence.NORMAL);
        event.setLatency(Cll.EventLatency.NORMAL);
        eventHandler.log(event);
        assert(normalEventHandlerOverride.eventCount == 1);
    }

    @Test
    public void testDeviceNotInSampleGroup() {
        // Create new event handler to reset the deviceId since we store it in event handler after first call
        CriticalEventHandlerOverride criticalEventHandlerOverride = new CriticalEventHandlerOverride(new CustomLogger(), filePath);
        NormalEventHandlerOverride normalEventHandlerOverride = new NormalEventHandlerOverride(new CustomLogger(), filePath);
        EventHandler eventHandler = new EventHandler(new ClientTelemetry("test"), new ArrayList<ICllEvents>(), new CustomLogger(), normalEventHandlerOverride, criticalEventHandlerOverride);
        SerializedEvent event = new SerializedEvent();
        event.setSerializedData(EventHelper.singleGoodJsonEvent);
        event.setDeviceId("000000099"); // Sample Group 53
        event.setSampleRate(SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.SAMPLERATE));
        event.setPersistence(Cll.EventPersistence.NORMAL);
        event.setLatency(Cll.EventLatency.NORMAL);
        eventHandler.log(event);
        assert(normalEventHandlerOverride.eventCount == 0);
    }

    @Test
    public void testAddToStorage() {
        CriticalEventHandlerOverride criticalEventHandlerOverride = new CriticalEventHandlerOverride(new CustomLogger(), filePath);
        NormalEventHandlerOverride normalEventHandlerOverride = new NormalEventHandlerOverride(new CustomLogger(), filePath);
        EventHandler eventHandler = new EventHandler(new ClientTelemetry("test"), new ArrayList<ICllEvents>(), new CustomLogger(), normalEventHandlerOverride, criticalEventHandlerOverride);
        SerializedEvent event = new SerializedEvent();
        event.setSerializedData(EventHelper.singleGoodJsonEvent);
        event.setDeviceId("0000000000");  // Sample Group 0
        event.setSampleRate(SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.SAMPLERATE));
        event.setPersistence(Cll.EventPersistence.NORMAL);
        event.setLatency(Cll.EventLatency.NORMAL);

        for(int i = 0; i < 10; i++) {
            eventHandler.log(event);
        }

        event.setPersistence(Cll.EventPersistence.CRITICAL);

        for(int i = 0; i < 10; i++) {
            eventHandler.log(event);
        }

        assert(normalEventHandlerOverride.eventCount == 10);
        assert(criticalEventHandlerOverride.eventCount == 10);
    }

    @Test
    public void testNullDeviceId() {
        CriticalEventHandlerOverride criticalEventHandlerOverride = new CriticalEventHandlerOverride(new CustomLogger(), filePath);
        NormalEventHandlerOverride normalEventHandlerOverride = new NormalEventHandlerOverride(new CustomLogger(), filePath);
        EventHandler eventHandler = new EventHandler(new ClientTelemetry("test"), new ArrayList<ICllEvents>(), new CustomLogger(), normalEventHandlerOverride, criticalEventHandlerOverride);
        SerializedEvent event = new SerializedEvent();
        event.setSerializedData(EventHelper.singleGoodJsonEvent);
        event.setDeviceId(null);  // Sample Group 0
        event.setSampleRate(SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.SAMPLERATE));
        event.setPersistence(Cll.EventPersistence.NORMAL);
        event.setLatency(Cll.EventLatency.NORMAL);
        eventHandler.log(event);
        assert(normalEventHandlerOverride.eventCount == 0);
    }
}
