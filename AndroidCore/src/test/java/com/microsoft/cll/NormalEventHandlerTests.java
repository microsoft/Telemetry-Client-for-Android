package com.microsoft.cll;

import com.microsoft.cll.Helpers.EventHelper;
import com.microsoft.cll.Helpers.FileHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.File;
import java.util.List;

import static org.junit.Assert.fail;

public class NormalEventHandlerTests {
    private NormalEventHandler normalEventHandler;
    private String filePath;
    private final String newLine = "\r\n";

    @Rule
    public TestName name = new TestName();

    @Before
    public void setup() {
        try {
            filePath = new File(".").getCanonicalPath() + File.separator + "cllEvents" + File.separator + name.getMethodName();

            FileHelper.cleanupFiles(filePath);

            File dir = new File(filePath);
            if(!dir.exists()) {
                dir.mkdirs();
            }

            normalEventHandler = new NormalEventHandler(new CustomLogger(), filePath);
        } catch (Exception e) {
            fail("Failed to setup test");
        }
    }

    @After
    public void cleanup() {
        if(normalEventHandler != null) {
            normalEventHandler.close();
        }

        FileHelper.cleanupFiles(filePath);
    }

    /**
     * Write 1 event to disk and test that it exists
     */
    @Test
    public void log1Event() {
        List<String> events = null;
        try {
            normalEventHandler.add(EventHelper.singleGoodJsonEvent);
            normalEventHandler.close();
            events = FileHelper.getNormalEventsOnDisk(filePath);
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
                normalEventHandler.add(EventHelper.singleGoodJsonEvent + newLine);
            }
            normalEventHandler.close();
            events = FileHelper.getNormalEventsOnDisk(filePath);
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
            normalEventHandler.add(EventHelper.singleGoodJsonEvent);
        } catch (Exception e) {
        }

        List<IStorage> storageList = normalEventHandler.getFilesForDraining();
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
                normalEventHandler.add(EventHelper.singleGoodJsonEvent + newLine);
            }
        } catch (Exception e) {
        }

        int eventCount = 0;
        List<IStorage> storageList = normalEventHandler.getFilesForDraining();
        for(IStorage storage : storageList) {
            eventCount += storage.drain().size();
            storage.close();
        }

        assert(eventCount == eventsToAdd);
    }

    @Test
    public void testDispose() {
        try {
            for(int i = 0; i < 1000; i++) {
                normalEventHandler.add(EventHelper.singleGoodJsonEvent);
            }
        } catch (Exception e) {
        }

        List<IStorage> storages = normalEventHandler.getFilesForDraining();
        for(IStorage storage : storages) {
            storage.discard();
        }

        assert(NormalEventHandler.totalStorageUsed.get() == 0);
    }
}
