package com.microsoft.cll;

import com.microsoft.telemetry.IJsonSerializable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Storage for events on disk
 */
public class FileStorage implements IStorage{
    protected static final SyncronizedArrayList<String> fileLockList = new SyncronizedArrayList<String>();
    private final String TAG = "FileStorage";
    private final ILogger logger;
    private final EventSerializer serializer;

    private boolean isOpen;
    private boolean isWritable;
    private int eventsWritten;
    private long fileSize;
    private String filePathAndName;
    private FileReader inputFile;
    private FileWriter outputFile;
    private BufferedReader reader;
    private AbstractHandler parent;

    /**
    This constructor is for opening a new file.
     */
    public FileStorage(String fileExtension, ILogger logger, String filePath, AbstractHandler parent) {
        this.eventsWritten      = 0;
        this.fileSize           = 0;
        this.filePathAndName    = filePath + File.separator + UUID.randomUUID() + fileExtension;
        this.logger             = logger;
        this.serializer         = new EventSerializer(logger);
        this.parent             = parent;

        int tries = 1;
        // If this filename is already taken try again
        while(!openFile()) {
            this.filePathAndName    = filePath + "/" + UUID.randomUUID() + fileExtension;
            tries++;

            if(tries >= 5) {
                logger.error(TAG, "Could not create a file");
                return;
            }
        }
    }

    /**
     * This constructor sets up a file for opening but does not open it (if we did we could potentially end up opening over 500 files at once with the current requirements,
     * this pushes into androids limits on max files open (1024)). All methods are safe and will check to see if the file is open before performing any operations.
     */
    public FileStorage(ILogger logger, String filePathAndName, AbstractHandler parent) throws Exception
    {
        this.logger             = logger;
        this.serializer         = new EventSerializer(logger);
        this.filePathAndName    = filePathAndName;
        this.parent             = parent;

        if(fileLockList.contains(filePathAndName)) {
            throw new Exception("Could not get lock for file");
        }
    }

    @Override
    public void add(IJsonSerializable event) throws FileFullException, IOException {
        String serializedEvent = serializer.serialize(event);
        add(serializedEvent);
    }

    @Override
    public void add(String serializedEvent) throws FileFullException, IOException {
        if(!isOpen || !isWritable) {
            logger.warn(TAG, "This file is not open or not writable");
            return;
        }

        if(!canAdd(serializedEvent)) {
            throw new FileFullException("The file is already full!");
        }

        outputFile.write(serializedEvent);
        eventsWritten++;
        fileSize += serializedEvent.length();
    }

    @Override
    public boolean canAdd(IJsonSerializable event) {
        String serializedEvent = serializer.serialize(event);
        return canAdd(serializedEvent);
    }

    @Override
    public boolean canAdd(String event) {
        if(!isOpen || !isWritable) {
            logger.warn(TAG, "This file is not open or not writable");
            return false;
        }

        int serializedSize = event.length();
        return ((eventsWritten < SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXEVENTSPERPOST)) && (serializedSize + fileSize < SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXEVENTSIZEINBYTES)));
    }

    @Override
    public List<String> drain() {
        List<String> drainedQueue = new ArrayList<String>();

        if(!isOpen) {
            try {
                if(openFile() == false) {
                    // If this check returns false that means that we weren't able to lock a file
                    // (most likely due to the fact that another writer is still writing to that file
                    return drainedQueue;
                }
            } catch (Exception e) {
                // Just return empty list for this file.
                logger.error(TAG, "Error opening file");
                return drainedQueue;
            }
        }

        try {
            // NOTE: This is very inefficient. Lots of strings will be created that will later need to be GC'd, is there a better way?
            String input = reader.readLine();
            while(input != null) {
                drainedQueue.add(input);
                input = reader.readLine();
            }
        } catch (Exception e) {
            logger.error(TAG, "Error reading from input file");
        }

        logger.info(TAG, "Read " + drainedQueue.size() + " events from file");
        return drainedQueue;
    }

    /**
     * Returns back the size of the file in bytes. Not the number of events in the file.
     */
    @Override
    public long size() {
        // If this file isn't open then just read the file size rather than opening all the buffers and pointers prematurely
        if(!isOpen) {
            File f = new File(filePathAndName);
            return f.length();
        }

        return fileSize;
    }

    /**
     * Closes the file and any readers if it is open
     * And deletes the file from disk
     */
    @Override
    public void discard() {
        logger.info(TAG, "Discarding file");

        close();

        parent.dispose(this);

        File f = new File(filePathAndName);
        f.delete();
    }

    /**
     * Flushes the current file to disk
     */
    public void flush() {
        if(!isOpen || !isWritable) {
            return;
        }

        try {
            outputFile.flush();
        } catch (Exception e) {
            logger.error(TAG, "Could not flush file");
        }
    }

    /**
     * Closes the writers and buffers associated with this file.
     */
    @Override
    public void close() {
        if(isOpen) {
            flush();

            fileLockList.remove(this.filePathAndName);

            try {
                if(isWritable) {
                    outputFile.close();

                } else {
                    inputFile.close();
                    reader.close();
                }

                isOpen = false;

            }catch (Exception e) {
                logger.error(TAG, "Error when closing file");
            }
        }
    }

    /**
     * Creates a new file if the filename doesn't currently exist on disk or opens the file if it does exist.
     */
    private boolean openFile() {
        boolean lockResult = getLock();

        if(lockResult == false) {
            logger.info(TAG, "Could not get lock for file");
            return false;
        }

        File f = new File(filePathAndName);
        boolean doesFileExist = f.exists(); // We store this value since calling into getFileLock will create the file and then the check below will always be true


        if(doesFileExist) {
            isWritable = false;
            try {
                inputFile = new FileReader(filePathAndName);
                reader = new BufferedReader(inputFile);
                fileSize = f.length();
            } catch (IOException e) {
                logger.error(TAG, "Event file was not found");
                return false;
            }
        } else {
            isWritable = true;
            logger.info(TAG, "Creating new file");

            try {
                outputFile = new FileWriter(filePathAndName);
            } catch (IOException e) {
                logger.error(TAG, "Error opening file");
                return false;
            }
        }

        isOpen = true;
        return true;
    }

    private boolean getLock() {
        return fileLockList.add(this.filePathAndName);
    }

    class FileFullException extends Exception {
        public FileFullException(String message) {
            super(message);
        }
    }
}