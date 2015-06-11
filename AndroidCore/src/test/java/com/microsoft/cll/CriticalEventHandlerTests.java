package com.microsoft.cll;

import com.microsoft.cll.Helpers.EventHelper;
import com.microsoft.cll.Helpers.FileHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.fail;

public class CriticalEventHandlerTests {
    private CriticalEventHandler criticalEventHandler;
    private String filePath;
    private final String newLine = "\r\n";

    @Before
    public void setup() {

        try {
            filePath = new File(".").getCanonicalPath() + File.separator + "cllEvents";
            File dir = new File(filePath);
            if(!dir.exists()) {
                dir.mkdir();
            }

            criticalEventHandler = new CriticalEventHandler(new CustomLogger(), filePath);
        } catch (Exception e) {
            fail("Failed to setup test");
        }
    }

    @After
    public void cleanup() {
        criticalEventHandler.close();
        File[] files = FileHelper.findFiles(FileHelper.normalEventFileExtension, filePath);
        for(File file : files) {
            if(!file.delete()) {
                fail("Could not delete file while cleaning up");
            }
        }

        files = FileHelper.findFiles(FileHelper.criticalEventFileExtension, filePath);
        for(File file : files) {
            if(!file.delete()) {
                fail("Could not delete file while cleaning up");
            }
        }

        // Delete all files on disk here
        File dir = new File(filePath);
        if(dir.exists()) {
            dir.delete();
        }
    }

    /**
     * Write 1 event to disk and test that it exists
     */
    @Test
    public void log1Event() {
        List<String> events = null;
        try {
            criticalEventHandler.add(EventHelper.singleGoodJsonEvent);
            events = FileHelper.getCriticalEventsOnDisk(filePath);
        } catch (Exception e) {

        }

        assert(events.size() == 1);
    }

    /**
     * Write 2000 events to disk and test that they exist
     */
    @Test
    public void log2000Events() {
        List<String> events = null;
        try {
            for(int i = 0; i < 2000; i++) {
                criticalEventHandler.add(EventHelper.singleGoodJsonEvent + newLine);
            }

            events = FileHelper.getCriticalEventsOnDisk(filePath);
        } catch (Exception e) {

        }

        assert(events.size() == 2000);
    }

    /**
     * Write 1 event to disk and test that we can read it back
     */
    @Test
    public void drain1Event() {
        List<String> events = null;
        try {
            criticalEventHandler.add(EventHelper.singleGoodJsonEvent);
        } catch (Exception e) {
        }

        List<IStorage> storageList = criticalEventHandler.getFilesForDraining();
        assert(storageList.size() == 1);
        events = storageList.get(0).drain();
        assert(events.size() == 1);
        storageList.get(0).close();
    }

    /**
     * Write 2000 events to disk and test that we can read them all back
     */
    @Test
    public void drain2000Events() {
        int eventsToAdd = 2000;
        try {
            for(int i = 0; i < eventsToAdd; i++) {
                criticalEventHandler.add(EventHelper.singleGoodJsonEvent + newLine);
            }
        } catch (Exception e) {
        }

        int eventCount = 0;
        List<IStorage> storageList = criticalEventHandler.getFilesForDraining();
        for(IStorage storage : storageList) {
            eventCount += storage.drain().size();
            storage.close();
        }

        assert(eventCount == eventsToAdd);
    }

    /**
     * Test Dropping a normal event file in favor of a critical event file if we run out of room
     * NOTE: We should really try to change the max storage space used here to cut down on having to add so many events.
     *       We can't use reflection since the setting is stored as <enum,object> and we don't have access to the enum.
     */
    @Test
    public void dropNormalFile() {
        NormalEventHandler normalEventHandler = new NormalEventHandler(new CustomLogger(), filePath);
        EventSerializer serializer = new EventSerializer(new CustomLogger());
        int eventSize = serializer.serialize(EventHelper.singleGoodABCEvent).length();
        final int totalDiskSpaceWeCanUse = 50*1024*1024; // 50 MB

        int currentSpaceUsed = 0;
        while(currentSpaceUsed + eventSize < totalDiskSpaceWeCanUse) {
            try {
                normalEventHandler.add(EventHelper.singleGoodJsonEvent);
            } catch (Exception e) {
            }

            currentSpaceUsed += eventSize;
        }

        normalEventHandler.close(); // Ensure any events in queued get written to disk
        int normalFileCountBefore = FileHelper.findNormalFilesOnDisk(filePath).length;
        try {
            criticalEventHandler.add(EventHelper.singleGoodJsonEvent);
        }catch (Exception e) {
        }

        int normalFileCountAfter = FileHelper.findNormalFilesOnDisk(filePath).length;
        assert(normalFileCountBefore-normalFileCountAfter == 1);
    }

    @Test
    public void testDispose() {
        try {
            for(int i = 0; i < 1000; i++) {
                criticalEventHandler.add(EventHelper.singleGoodJsonEvent);
            }
        } catch (Exception e) {
        }

        List<IStorage> storages = criticalEventHandler.getFilesForDraining();
        for(IStorage storage : storages) {
            storage.discard();
        }

        assert(CriticalEventHandler.totalStorageUsed.get() == 0);
    }
}