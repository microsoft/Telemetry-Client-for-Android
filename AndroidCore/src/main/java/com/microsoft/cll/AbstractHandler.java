package com.microsoft.cll;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An Abstract Handler class
 */
public abstract class AbstractHandler {
    private final String TAG = "AbstractHandler";
    protected final ILogger logger;
    protected FileStorage fileStorage;
    protected String filePath;

    protected final static String criticalEventFileExtension = ".crit.cllevent";
    protected final static String normalEventFileExtension = ".norm.cllevent";
    protected static AtomicLong totalStorageUsed = new AtomicLong(0);

    public AbstractHandler(ILogger logger, String filePath) {
        this.filePath       = filePath;
        this.logger         = logger;

        setFileStorageUsed();
    }

    public abstract void add(String event) throws IOException, FileStorage.FileFullException;

    public abstract List<IStorage> getFilesForDraining();

    public abstract void close();

    public abstract void dispose(IStorage storage);

    /**
     * Checks to see if there is room to add this event
     * @param serializedEvent The event to add
     * @return True if we can or false if we can't
     */
    public boolean canAdd(String serializedEvent) {
        if(totalStorageUsed.get() + serializedEvent.length() <= SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXFILESSPACE)) {
            return true;
        }

        return false;
    }

    /**
     * Get all files that aren't currently being written to
     * @return A list of files with a specific extension
     */
    protected List<IStorage> getFilesByExtensionForDraining(final String fileExtension) {
        List<IStorage> fullFiles = new ArrayList<IStorage>();
        for(File file : findExistingFiles(fileExtension)) {
            try {
                IStorage storage = new FileStorage(logger, file.getAbsolutePath(), this);
                fullFiles.add(storage);
                storage.close();
            }catch (Exception e) {
                logger.info(TAG, "File " + file.getName() + " is in use still");
            }
        }

        return fullFiles;
    }

    /**
     * Looks for any existing critical cll event files on disk
     * @param fileExtension The file extension of files to lok for
     * @return An array of files
     */
    protected File[] findExistingFiles(final String fileExtension) {
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                if (lowercaseName.endsWith(fileExtension)) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        return new File(filePath).listFiles(filter);
    }

    /**
     * Sets the storage used by all files on disk
     */
    private void setFileStorageUsed() {
        totalStorageUsed.set(0);

        // Get space used by critical files
        for(File file : findExistingFiles(criticalEventFileExtension)) {
            totalStorageUsed.getAndAdd(file.length());
        }

        // Get space used by normal files
        for(File file : findExistingFiles(normalEventFileExtension)) {
            totalStorageUsed.getAndAdd(file.length());
        }
    }
}