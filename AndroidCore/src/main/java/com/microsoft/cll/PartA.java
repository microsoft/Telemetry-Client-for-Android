/**
 * Copyright Microsoft Corporation 2014
 * All Rights Reserved
 */

package com.microsoft.cll;

import com.microsoft.telemetry.Base;
import com.microsoft.telemetry.Data;
import com.microsoft.telemetry.Domain;
import com.microsoft.telemetry.Envelope;
import com.microsoft.telemetry.Extension;
import com.microsoft.telemetry.extensions.android;
import com.microsoft.telemetry.extensions.app;
import com.microsoft.telemetry.extensions.device;
import com.microsoft.telemetry.extensions.os;
import com.microsoft.telemetry.extensions.user;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;

/**
 * PopulatePartA provides values used for Part A population of the Envelope.
 */
public abstract class PartA {
    protected final ILogger logger;
    protected final user userExt;
    protected final device deviceExt;
    protected final os osExt;
    protected final android androidExt;
    protected final app appExt;
    private final String csVer = "2.1";
    private final String TAG = "PartA";
    private final String salt = "oRq=MAHHHC~6CCe|JfEqRZ+gc0ESI||g2Jlb^PYjc5UYN2P 27z_+21xxd2n";
    private final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private final AtomicLong seqCounter;
    private EventSerializer serializer;
    protected String appId;
    protected String appVer;
    protected String osVer;
    protected String osName;
    protected String uniqueId;
    private long epoch;
    private long flags;
    private String iKey;
    private boolean useLagacyCS = false;

    /**
     * Set variables that will be used across all Part A's and constant
     */
    public PartA(ILogger logger, String iKey) {
        this.logger = logger;
        this.iKey = iKey;
        this.seqCounter = new AtomicLong(0);
        this.serializer = new EventSerializer(logger);

        userExt = new user();
        deviceExt = new device();
        osExt = new os();
        appExt = new app();
        appExt.setExpId("");
        androidExt = new android();
        androidExt.setLibVer(BuildNumber.BuildNumber);

        Random random = new Random();
        epoch = random.nextLong();
    }

    /**
     * Populate the given Part A schema with the current collection level
     *
     * @param base The base event to package in the Envelope
     */
    public SerializedEvent populate(final Base base, String cV, Map<String, String> tags) {
        int sampleRate = Integer.parseInt(SettingsStore.getSetting(base, SettingsStore.Settings.SAMPLERATE).toString());
        Cll.EventPersistence persistence = Cll.EventPersistence.valueOf(SettingsStore.getSetting(base, SettingsStore.Settings.PERSISTENCE).toString().toUpperCase());
        Cll.EventLatency latency = Cll.EventLatency.valueOf(SettingsStore.getSetting(base, SettingsStore.Settings.LATENCY).toString().toUpperCase());

        if(useLagacyCS) {
            com.microsoft.telemetry.cs2.Envelope envelope = populateLegacyEnvelope(base, cV, sampleRate, persistence, latency, tags);
            return populateSerializedEvent(serializer.serialize(envelope), persistence, latency, envelope.getSampleRate(), envelope.getDeviceId());
        } else {
            Envelope envelope = populateEnvelope(base, cV, sampleRate, persistence, latency);
            return populateSerializedEvent(serializer.serialize(envelope), persistence, latency, envelope.getPopSample(), deviceExt.getLocalId());
        }
    }

    public Envelope populateEnvelope(final Base base, String cV, int sampleRate, Cll.EventPersistence persistence, Cll.EventLatency latency) {
        final Envelope envelope = new Envelope();
        setBaseType(base);
        envelope.setVer(csVer);
        envelope.setTime(getDateTime());
        envelope.setName(base.QualifiedName);
        envelope.setPopSample(sampleRate);
        envelope.setEpoch(String.valueOf(epoch));
        envelope.setSeqNum(setSeq());
        envelope.setOs(osName);
        envelope.setOsVer(osVer);
        envelope.setData(base);
        envelope.setAppId(appId);
        envelope.setAppVer(appVer);
        envelope.setCV(cV);
        envelope.setFlags(setFlags(persistence, latency));
        envelope.setIKey(iKey);
        envelope.setExt(createExtensions());
        return envelope;
    }

    public com.microsoft.telemetry.cs2.Envelope populateLegacyEnvelope(final Base base, String cV, int sampleRate, Cll.EventPersistence persistence, Cll.EventLatency latency, Map<String, String> tags) {
        if(tags == null) {
            tags = new HashMap<String, String>();
        }

        tags.put("cV", cV);

        com.microsoft.telemetry.cs2.Envelope envelope = new com.microsoft.telemetry.cs2.Envelope();
        envelope.setVer(1);
        envelope.setTime(getDateTime());
        envelope.setName(base.QualifiedName);
        envelope.setSampleRate(sampleRate);
        envelope.setSeq(String.valueOf(epoch) + ":" + String.valueOf(setSeq()));
        envelope.setOs(osName);
        envelope.setOsVer(osVer);
        envelope.setData(base);
        envelope.setAppId(appId);
        envelope.setAppVer(appVer);
        envelope.setTags(tags);
        envelope.setFlags(setFlags(persistence, latency));
        envelope.setIKey(iKey);
        return envelope;
    }

    /**
     * Set's whether we should use the legacy part A fields or not.
     * @param value True if we should, false if we should not
     */
    void useLagacyCS(boolean value) {
        this.useLagacyCS = value;
    }

    protected abstract void setDeviceInfo();

    protected abstract void setOs();

    protected abstract void setAppInfo();

    protected abstract void setUserId();

    protected abstract void PopulateConstantValues();

    protected String HashStringSha256(String str) {
        try {
            // Get a Sha256 digest
            MessageDigest hash = MessageDigest.getInstance("SHA-256");
            hash.reset();
            hash.update(str.getBytes());
            hash.update(salt.getBytes());
            byte[] hashed = hash.digest();
            return bytesToHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            // All android devices support SHA256
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Sets the experiment id
     * @param id The experiment id
     */
    protected void setExpId(String id) {
        appExt.setExpId(id);
    }

    /**
     * Converts a byte[] array to a readable hex String, using some bitwise
     * magic. see: http://stackoverflow.com/questions/9655181/convert-from-byte
     * -array-to-hex-string-in-java
     *
     * @param bytes Array of bytes
     * @return A string that is the hexidecimal representation of the byte array
     */
    private String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private LinkedHashMap<String, Extension> createExtensions() {
        LinkedHashMap<String, Extension> extensions = new LinkedHashMap<String, Extension>();
        extensions.put("user", userExt);
        extensions.put("os", osExt);
        extensions.put("device", deviceExt);
        extensions.put("android", androidExt);

        if(appExt.getExpId() != null && !appExt.getExpId().equals("")) {
            extensions.put("app", appExt);
        }

        return extensions;
    }

    /**
     * Gets the current date time
     *
     * @return the date time in string form
     */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return dateFormat.format(new Date()).toString();
    }

    /**
     * Sets the base type using reflection if a part b is present
     *
     * @param base The base object
     */
    private void setBaseType(Base base) {
        try {
            String baseType = ((Domain) ((Data) base).getBaseData()).QualifiedName;
            base.setBaseType(baseType);
        } catch (ClassCastException e) {
            logger.error(TAG, "This event doesn't extend data");
        }
    }

    /**
     * Sets the flags given persistence and latency
     *
     * @param persistence
     * @param latency
     */
    private long setFlags(Cll.EventPersistence persistence, Cll.EventLatency latency) {
        flags = 0;
        // Set Latency
        flags |= latency.getCode() << 8;
        // Set persistence
        flags |= persistence.getCode();

        return flags;
    }

    /**
     * Sets the sequence for this event
     */
    private long setSeq() {
        long uploadId = seqCounter.incrementAndGet();
        return uploadId;
    }

    private SerializedEvent populateSerializedEvent(String eventData, Cll.EventPersistence persistence, Cll.EventLatency latency, double sampleRate, String deviceId) {
        SerializedEvent event = new SerializedEvent();
        event.setSerializedData(eventData);
        event.setSampleRate(sampleRate);
        event.setDeviceId(deviceExt.getLocalId());
        event.setPersistence(persistence);
        event.setLatency(latency);
        return event;
    }
}