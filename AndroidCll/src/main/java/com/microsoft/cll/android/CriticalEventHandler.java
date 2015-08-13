package com.microsoft.cll.android;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Critical events are never queued in memory, instead they are directly written to disk.
 */
public class CriticalEventHandler extends AbstractHandler
{
    private final String TAG = "CriticalEventHandler";
    private long counter = 0;

    /**
     * Creates an event handler for critical events
     * @param logger A logger to use
     * @param filePath The filepath where we will store events
     */
    public CriticalEventHandler(ILogger logger, String filePath)
    {
        super(logger, filePath);
        this.fileStorage        = new FileStorage(criticalEventFileExtension, logger, filePath, this);
    }

    /**
     * Adds a critical event to disk storage
     * @param event The event to add
     * @throws Exception An exception if we cannot add
     */
    @Override
    public synchronized void add(String event) throws IOException, FileStorage.FileFullException
    {
        int attempts = 0;
        while(!canAdd(event)) {

            // If we don't limit this then we can potentially block a thread forever trying to add
            if(attempts >= SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXCRITICALCANADDATTEMPTS)) {
                return;
            }

            logger.warn(TAG, "Out of storage space. Attempting to drop normal file");
            dropNormalFile();
            attempts++;
        }

        // If file is full flush and close file, then open a new one
        if (!fileStorage.canAdd(event)) {
            logger.info(TAG, "Closing full file and opening a new one");
            fileStorage.close();
            fileStorage = new FileStorage(criticalEventFileExtension, logger, filePath, this);
        }

        fileStorage.add(event);
        totalStorageUsed.getAndAdd(event.length());
        fileStorage.flush();
        counter++;
    }

    /**
     * 1) Close the current file, so it will get uploaded immediately.
     * 2) Get the list of all non open files on disk.
     * 3) Create a new file for any incoming events
     * @return The list of non open critical event files
     */
    @Override
    public synchronized List<IStorage> getFilesForDraining()
    {
        List<IStorage> storageList;

        // Don't close the current file if it is empty
        if(fileStorage.size() > 0) {
            fileStorage.close();
            storageList = getFilesByExtensionForDraining(criticalEventFileExtension);
            fileStorage = new FileStorage(criticalEventFileExtension, logger, filePath, this);
        } else {
            storageList = getFilesByExtensionForDraining(criticalEventFileExtension);
        }

        return storageList;
    }

    /**
     * Nothing to do here since we always flush immediately after writing critical events
     */
    @Override
    public void close()
    {
        logger.info(TAG, "Closing critical file");
        fileStorage.close();
    }

    /**
     * Decrement the storage used by the file we are discarding
     */
    @Override
    public void dispose(IStorage storage)
    {
        this.totalStorageUsed.getAndAdd(-1 * storage.size());
    }

    /**
     * Drop the least recent normal file from the disk (unless it is the current normal file that is being written to)
     */
    private void dropNormalFile()
    {
        File[] files = findExistingFiles(normalEventFileExtension);
        if(files.length < 2) {
            // We do 2 because we want to ensure that there are files, and also we don't want to delete the
            // current normal file since it may be getting written to right now and deleting it will cause
            // problems with the current lock state
            logger.info(TAG, "There are no normal files to delete");
            return;
        }

        long lastModified  = files[0].lastModified();
        File lastModifiedFile = files[0];
        for(File file : files) {
            // Newer files have a larger lastModified value. So we want to find the file with the smallest value
            if(file.lastModified() < lastModified) {
                lastModified = file.lastModified();
                lastModifiedFile = file;
            }
        }

        totalStorageUsed.getAndAdd(-1 * lastModifiedFile.length());
        lastModifiedFile.delete();

    }
}