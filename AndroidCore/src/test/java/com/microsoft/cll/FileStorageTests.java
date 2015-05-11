package com.microsoft.cll;

import com.microsoft.cll.Helpers.EventHelper;
import com.microsoft.cll.Helpers.FileHelper;
import com.microsoft.cll.Overrides.NormalEventHandlerOverride;
import com.microsoft.telemetry.IJsonSerializable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class FileStorageTests {
    private String filePath;
    @Before
    public void setup() {
        try {
            filePath = new File(".").getCanonicalPath() + File.separator + "cllEvents";
            File dir = new File(filePath);
            if(!dir.exists()) {
                dir.mkdir();
            }
        } catch (Exception e) {}
    }

    @After
    public void cleanup() {
        File[] files = FileHelper.findFiles(FileHelper.normalEventFileExtension, filePath);
        for(File file : files) {
            file.delete();
        }

        files = FileHelper.findFiles(FileHelper.criticalEventFileExtension, filePath);
        for(File file : files) {
            file.delete();
        }

        // Delete all files on disk here
        File dir = new File(filePath);
        if(dir.exists()) {
            dir.delete();
        }
    }

    @Test
    public void testWriteAndDrainInUseFile() {
        FileStorage fs = new FileStorage(".norm.cllEvent", new CustomLogger(), filePath, null);
        try {
            fs.add(EventHelper.generateABCEvent());
        } catch (Exception e) {}

        fs.flush();
        int eventCount = fs.drain().size();
        assert(eventCount == 0);
        fs.close();
    }

    @Test
    public void testWritingToClosedFile() {
        FileStorage fs = new FileStorage(".norm.cllEvent", new CustomLogger(), filePath, null);
        try {
            fs.add(EventHelper.generateABCEvent());
        } catch (Exception e) {}

        fs.flush();
        fs.close();

        try {
            fs.add(EventHelper.generateABCEvent());
        } catch (Exception e) {
            int i = 0;
        }

        int eventCount = FileHelper.getAllEventsOnDisk(filePath).size();
        assert(eventCount == 1);
    }

    @Test
    public void testFileDiscard() {
        AbstractHandler abstractHandler = new NormalEventHandlerOverride(new CustomLogger(), filePath);
        FileStorage fs = new FileStorage(".norm.cllEvent", new CustomLogger(), filePath, abstractHandler);
        try {
            for(int i = 0; i < 50; i++) {
                fs.add(EventHelper.generateABCEvent());
            }
        } catch (Exception e) {}

        fs.flush();
        fs.close();
        int eventCount = FileHelper.getAllEventsOnDisk(filePath).size();
        assert(eventCount == 50);
        fs.discard();
        eventCount = FileHelper.getAllEventsOnDisk(filePath).size();
        assert(eventCount == 0);
        abstractHandler.close();
    }

    @Test
    public void testFileSize() {
        FileStorage fs = new FileStorage(".norm.cllEvent", new CustomLogger(), filePath, null);
        EventSerializer serializer = new EventSerializer(new CustomLogger());
        int totalSize = 0;
        try {
            for(int i = 0; i < 50; i++) {
                IJsonSerializable event = EventHelper.generateABCEvent();
                fs.add(event);
                totalSize += serializer.serialize(event).length();
            }
        } catch (Exception e) {}

        fs.flush();
        assert(fs.size() == totalSize);
        fs.close();

    }

    @Test
    public void testFileSizeLimit() {
        FileStorage fs = new FileStorage(".norm.cllEvent", new CustomLogger(), filePath, null);
        EventSerializer serializer = new EventSerializer(new CustomLogger());
        IJsonSerializable event = EventHelper.generateABCEvent();
        int eventSize = serializer.serialize(event).length();
        int totalSize = 0;
        boolean catchException = false;
        try {
            while (totalSize + eventSize < SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXEVENTSIZEINBYTES)) {
                fs.add(event);
                totalSize += eventSize;
            }
        }catch (Exception e) {
            System.out.println("Error Adding Event");
        }

        try {
            fs.add(event);
        } catch (Exception e) {
            catchException = true;
        }

        fs.close();
        assert(catchException == true);
    }

    @Test
    public void testFileEventCountLimit() {
        FileStorage fs = new FileStorage(".norm.cllEvent", new CustomLogger(), filePath, null);
        String event = EventHelper.partialEvent;
        int eventSize = event.length();
        int totalSize = 0;
        boolean catchException = false;
        try {
            for(int i = 0; i < SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXEVENTSPERPOST); i++) {
                fs.add(event);
                totalSize += eventSize;
            }
        }catch (Exception e) {
            System.out.println("Error Adding Event");
        }

        try {
            fs.add(event);
        } catch (Exception e) {
            catchException = true;
        }

        fs.close();
        assert(catchException == true);
    }
}
