package com.microsoft.cll;

/**
 * This class handles batching events for upload to Vortex
 */
public class EventBatcher {
    private StringBuilder eventString;
    private int numberOfEvents;
    private final String newLine = "\r\n";
    private int size;

    /**
     * Creates an event batcher of a specific size
     * @param size
     */
    public EventBatcher(int size)
    {
        this.size = size;
        eventString = new StringBuilder(size);
        numberOfEvents = 0;
    }

    /**
     * Creates an event batcher using the default size from settings
     */
    public EventBatcher()
    {
        this.size = SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXEVENTSIZEINBYTES);
        eventString = new StringBuilder(size);
        numberOfEvents = 0;
    }

    /**
     * Checks to see if we can add this event to the batch without breaking the size and number of events constraints
     */
    protected boolean canAddToBatch(String serializedEvent)
    {
        if(eventString.length() + newLine.length() + serializedEvent.length() > size ||
                numberOfEvents >= (SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXEVENTSPERPOST))) {
            return false;
        }

        return true;
    }

    /**
     * Adds the event to the batch
     */
    public void addEventToBatch(String serializedEvent) throws BatchFullException
    {
        if(!canAddToBatch(serializedEvent)) {
            throw new BatchFullException("Batch size too large! Send this batch first then retry");
        }

        eventString.append(serializedEvent).append(newLine);
        numberOfEvents++;
    }

    /**
     * Returns the batched events
     */
    public String getBatchedEvents()
    {
        String batchedEvents = eventString.toString();
        eventString.setLength(0);
        numberOfEvents = 0;
        return batchedEvents;
    }

    public class BatchFullException extends Exception {
        BatchFullException(String message) {
            super(message);
        }
    }
}
