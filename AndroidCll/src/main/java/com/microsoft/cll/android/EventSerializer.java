package com.microsoft.cll.android;

import com.microsoft.telemetry.IJsonSerializable;

import java.io.IOException;
import java.io.StringWriter;

/**
 * This class handles the serialization of events
 */
public class EventSerializer {
    private final ILogger logger;
    private final String TAG = "EventSerializer";

    /**
     * Creates an instance of the event serializer object
     * @param logger The logger to log with
     */
    public EventSerializer(ILogger logger) {
        this.logger = logger;
    }

    /**
     * Serializes the event to json
     */
    public String serialize(IJsonSerializable event) {
        StringWriter writer = new StringWriter();

        try {
            event.serialize(writer);
        } catch (IOException e) {
            logger.error(TAG, "IOException when serializing");
        }

        String serialized = writer.toString() + "\r\n";
        logger.info(TAG, serialized);

        return serialized;
    }
}