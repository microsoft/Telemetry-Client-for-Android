package com.microsoft.cll.android;

import com.microsoft.cll.android.Helpers.CustomStorageHelper;
import com.microsoft.cll.android.Helpers.EventHelper;
import com.microsoft.cll.android.Overrides.EventSenderOverride;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static org.junit.Assert.fail;

public class EventQueueWriterTests {
    private EventSenderOverride eventSenderOverride;
    private URL url;
    private String filePath;

    @Before
    public void setup() {
        try {
            url = new URL("https://vortex.data.microsoft.com/collect/v1");
            eventSenderOverride = new EventSenderOverride(url);

            filePath = new File(".").getCanonicalPath() + File.separator + "cllEvents";
            File dir = new File(filePath);
            if(!dir.exists()) {
                dir.mkdir();
            }
        } catch (Exception e) {
            fail("Failed to setup test");
        }
    }

    @After
    public void cleanUp() {

    }

    @Test
    public void sendRealTimEvent() {
        SerializedEvent serializedEvent = new SerializedEvent();
        serializedEvent.setSerializedData(EventHelper.singleGoodJsonEvent);
        EventQueueWriter eventQueueWriter = new EventQueueWriter(url, serializedEvent, new ClientTelemetry("test"), new ArrayList<ICllEvents>(), new CustomLogger(), null, null);
        eventQueueWriter.setSender(eventSenderOverride);
        eventQueueWriter.run();
        assert(eventSenderOverride.getNumberOfEventsAccepted() == 1);
    }

    @Test
    public void sendNormalEvents() {
        CustomStorageHelper fs = new CustomStorageHelper();
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        try {
            for(int i = 0; i < 50; i++) {
                fs.add(EventHelper.generateABCEvent());
            }
        } catch (Exception e) {
        }

        ArrayList<IStorage> storages = new ArrayList<IStorage>();
        storages.add(fs);

        EventQueueWriter eventQueueWriter = new EventQueueWriter(url, storages, new ClientTelemetry("test"), new ArrayList<ICllEvents>(), new CustomLogger(), scheduledExecutorService);
        eventQueueWriter.setSender(eventSenderOverride);
        eventQueueWriter.run();

        assert(eventSenderOverride.getNumberOfEventsAccepted() == 50);
    }

    /**
     * The goal of this method is to test the upload retry backoff logic
     */
    @Test
    public void sendEventsWithoutNetwork() {
        CustomStorageHelper fs = new CustomStorageHelper();
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        try {
            for(int i = 0; i < 50; i++) {
                fs.add(EventHelper.generateABCEvent());
            }
        } catch (Exception e) {
        }

        ArrayList<IStorage> storages = new ArrayList<IStorage>();
        storages.add(fs);

        eventSenderOverride.disableNetwork = true;

        EventQueueWriter eventQueueWriter = new EventQueueWriter(url, storages, new ClientTelemetry("test"), new ArrayList<ICllEvents>(), new CustomLogger(), scheduledExecutorService);
        eventQueueWriter.setSender(eventSenderOverride);
        scheduledExecutorService.submit(eventQueueWriter);
        try {
            Thread.sleep(65000);
        }catch (Exception e) {}

        // Ensure there was at least one callback
        assert(eventSenderOverride.getNumberOfSendAttempts() > 1);
    }

    @Test
    public void testBackoffInterval() {
        EventQueueWriter eventQueueWriter = new EventQueueWriter(url, null, new ClientTelemetry("test"), new ArrayList<ICllEvents>(), new CustomLogger(), null);
        int defaultConstant = SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.CONSTANTFORRETRYPERIOD);
        int defaultBase = SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.BASERETRYPERIOD);
        int max = SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXRETRYPERIOD);
        int interval = eventQueueWriter.generateBackoffInterval();

        assert (interval == defaultConstant*Math.pow(defaultBase, 0));

        interval = eventQueueWriter.generateBackoffInterval();
        assert (interval == defaultConstant*Math.pow(defaultBase, 0)
                || interval == defaultConstant*Math.pow(defaultBase, 1));

        interval = eventQueueWriter.generateBackoffInterval();
        assert (interval == defaultConstant*Math.pow(defaultBase, 0)
                || interval == defaultConstant*Math.pow(defaultBase, 1)
                || interval == defaultConstant*Math.pow(defaultBase, 2));

        interval = eventQueueWriter.generateBackoffInterval();
        assert (interval == defaultConstant*Math.pow(defaultBase, 0)
                || interval == defaultConstant*Math.pow(defaultBase, 1)
                || interval == defaultConstant*Math.pow(defaultBase, 2)
                || interval == defaultConstant*Math.pow(defaultBase, 3));

        interval = eventQueueWriter.generateBackoffInterval();
        assert (interval == defaultConstant*Math.pow(defaultBase, 0)
                || interval == defaultConstant*Math.pow(defaultBase, 1)
                || interval == defaultConstant*Math.pow(defaultBase, 2)
                || interval == defaultConstant*Math.pow(defaultBase, 3)
                || interval == defaultConstant*Math.pow(defaultBase, 4));

        for(int i = 0; i < 50; i++) {
            assert (eventQueueWriter.generateBackoffInterval() < max);
        }
    }
}
