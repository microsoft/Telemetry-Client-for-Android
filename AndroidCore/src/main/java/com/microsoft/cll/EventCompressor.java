package com.microsoft.cll;

import java.util.Arrays;
import java.util.zip.Deflater;

/**
 * Handles compressing events using the deflate algorithm
 * before uploading them.
 */
public class EventCompressor
{
    private final String TAG = "EventCompressor";
    private final ILogger logger;

    /**
     * Creates an event compressor which compresses events using the
     * @param logger
     */
    public EventCompressor(ILogger logger)
    {
        this.logger = logger;
    }

    /**
     * Compresses the given serialized, batched event string
     * @param events The event string
     * @return A compressed version of the event string
     */
    public byte[] compress(String events)
    {
        try {
            byte[] input = events.getBytes("UTF-8");
            byte[] output = new byte[SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXEVENTSIZEINBYTES)];

            Deflater compressor = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
            compressor.setInput(input);
            compressor.finish();
            int compressedDataLength = compressor.deflate(output);
            if(compressedDataLength >= SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXEVENTSIZEINBYTES)) {
                logger.error(TAG, "Compression resulted in a string of at least the max event buffer size of Vortex. Most likely this means we lost part of the string.");
                return null;
            }

            return Arrays.copyOfRange(output, 0, compressedDataLength);
        } catch (Exception e) {
            logger.error(TAG, "Could not compress events");
        }

        return null;
    }
}
