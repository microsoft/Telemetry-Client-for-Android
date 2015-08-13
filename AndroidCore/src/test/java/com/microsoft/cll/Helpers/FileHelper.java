package com.microsoft.cll.Helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {
    public final static String criticalEventFileExtension = ".crit.cllevent";
    public final static String normalEventFileExtension = ".norm.cllevent";

    public static List<String> getNormalEventsOnDisk(String filePath) {
        List<String> events = new ArrayList<String>();
        for(File file : findFiles(normalEventFileExtension, filePath)) {
            events.addAll(readEventsFromFile(file));
        }

        return events;
    }

    public static List<String> getCriticalEventsOnDisk(String filePath) {
        List<String> events = new ArrayList<String>();
        for(File file : findFiles(criticalEventFileExtension, filePath)) {
            events.addAll(readEventsFromFile(file));
        }

        return events;
    }

    public static List<String> getAllEventsOnDisk(String filePath) {
        List<String> list = getNormalEventsOnDisk(filePath);
        list.addAll(getCriticalEventsOnDisk(filePath));
        return list;
    }

    public static File[] findNormalFilesOnDisk(String filePath) {
        return findFiles(normalEventFileExtension, filePath);
    }

    public static File[] findCriticalFilesOnDisk(String filePath) {
        return findFiles(criticalEventFileExtension, filePath);
    }

    public static File[] findFiles(final String fileExtension, String filePath) {
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

        File[] files = new File(filePath).listFiles(filter);
        if(files == null) {
            files = new File[] {};
        }

        return files;
    }

    private static List<String> readEventsFromFile(File file) {
        List<String> list = new ArrayList<String>();

        try {
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String input = bufferedReader.readLine();
            while(input != null) {
                list.add(input);
                input = bufferedReader.readLine();
            }

            bufferedReader.close();
            reader.close();
        } catch (Exception e) {

        }

        return  list;
    }

    public static void cleanupFiles(String filePath) {
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
}
