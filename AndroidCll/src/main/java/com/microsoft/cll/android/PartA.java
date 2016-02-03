package com.microsoft.cll.android;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    protected final AtomicLong seqCounter;
    private final String csVer = "2.1";
    private final String TAG = "PartA";
    private final String salt = "oRq=MAHHHC~6CCe|JfEqRZ+gc0ESI||g2Jlb^PYjc5UYN2P 27z_+21xxd2n";
    private final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private EventSerializer serializer;
    protected String appId;
    protected String appVer;
    protected String osVer;
    protected String osName;
    protected String uniqueId;
    private long epoch;
    private long flags;
    private String iKey;
    private boolean useLegacyCS = false;
    private Random random;
    private CorrelationVector correlationVector;

    /**
     * Set variables that will be used across all Part A's and constant
     */
    public PartA(ILogger logger, String iKey, CorrelationVector correlationVector) {
        this.logger = logger;
        this.iKey = iKey;
        this.correlationVector = correlationVector;
        seqCounter = new AtomicLong(0);
        serializer = new EventSerializer(logger);

        userExt = new user();
        deviceExt = new device();
        osExt = new os();
        appExt = new app();
        androidExt = new android();
        androidExt.setLibVer(BuildConfig.VERSION_NAME);

        random = new Random();
        epoch = random.nextLong();
    }

    /**
     * Populate the given Part A schema with the current collection level
     *
     * @param base The base event to package in the Envelope
     */
    public SerializedEvent populate(final Base base, Map<String, String> tags, EventSensitivity... sensitivities) {
        int sampleRate = Integer.parseInt(SettingsStore.getSetting(base, SettingsStore.Settings.SAMPLERATE).toString());
        Cll.EventPersistence persistence = Cll.EventPersistence.valueOf(SettingsStore.getSetting(base, SettingsStore.Settings.PERSISTENCE).toString().toUpperCase());
        Cll.EventLatency latency = Cll.EventLatency.valueOf(SettingsStore.getSetting(base, SettingsStore.Settings.LATENCY).toString().toUpperCase());

        if(useLegacyCS) {
            com.microsoft.telemetry.cs2.Envelope envelope = populateLegacyEnvelope(base, correlationVector.GetValue(), sampleRate, persistence, latency, tags, sensitivities);
            return populateSerializedEvent(serializer.serialize(envelope), persistence, latency, envelope.getSampleRate(), envelope.getDeviceId());
        } else {
            Envelope envelope = populateEnvelope(base, correlationVector.GetValue(), sampleRate, persistence, latency, sensitivities);
            return populateSerializedEvent(serializer.serialize(envelope), persistence, latency, envelope.getPopSample(), deviceExt.getLocalId());
        }
    }

    public Envelope populateEnvelope(final Base base, String cV, int sampleRate, Cll.EventPersistence persistence, Cll.EventLatency latency, EventSensitivity... sensitivities) {
        final Envelope envelope = new Envelope();
        setBaseType(base);
        envelope.setVer(csVer);
        envelope.setTime(getDateTime());
        envelope.setName(base.QualifiedName);
        envelope.setPopSample(sampleRate);
        envelope.setEpoch(String.valueOf(epoch));
        envelope.setSeqNum(setSeq(sensitivities));
        envelope.setOs(osName);
        envelope.setOsVer(osVer);
        envelope.setData(base);
        envelope.setAppId(appId);
        envelope.setAppVer(appVer);

        if(correlationVector.isInitialized) {
            envelope.setCV(cV);
        }

        envelope.setFlags(setFlags(persistence, latency, base, sensitivities));
        envelope.setIKey(iKey);
        envelope.setExt(createExtensions());

        scrubPII(envelope, sensitivities);
        return envelope;
    }

    public com.microsoft.telemetry.cs2.Envelope populateLegacyEnvelope(final Base base, String cV, int sampleRate, Cll.EventPersistence persistence, Cll.EventLatency latency, Map<String, String> tags, EventSensitivity... sensitivities) {
        if(tags == null) {
            tags = new HashMap<String, String>();
        }

        if(correlationVector.isInitialized) {
            tags.put("cV", cV);
        }

        com.microsoft.telemetry.cs2.Envelope envelope = new com.microsoft.telemetry.cs2.Envelope();
        envelope.setVer(1);
        envelope.setTime(getDateTime());
        envelope.setName(base.QualifiedName);
        envelope.setSampleRate(sampleRate);
        envelope.setSeq(String.valueOf(epoch) + ":" + String.valueOf(setSeq(sensitivities)));
        envelope.setOs(osName);
        envelope.setOsVer(osVer);
        envelope.setData(base);
        envelope.setAppId(appId);
        envelope.setAppVer(appVer);
        envelope.setTags(tags);
        envelope.setFlags(setFlags(persistence, latency, base, sensitivities));
        envelope.setIKey(iKey);
        envelope.setUserId(userExt.getLocalId());
        envelope.setDeviceId(deviceExt.getLocalId());

        return envelope;
    }

    void setAppUserId(String userId) {
        if(userId == null) {
            appExt.setUserId(null);
            return;
        }

        // Validate userId
        Pattern pattern = Pattern.compile("^((c:)|(i:)|(w:)).*");
        Matcher matcher = pattern.matcher(userId);
        if(!matcher.find()) {
            // If userId does not follow a valid format set it back to null
            appExt.setUserId(null);
            logger.warn(TAG, "The userId supplied does not match the required format.");
            return;
        }

        // Set UserId
        appExt.setUserId(userId);
    }

    String getAppUserId() {
        return appExt.getUserId();
    }

    /**
     * Sets whether we should use the legacy part A fields or not.
     * @param value True if we should, false if we should not
     */
    void useLegacyCS(boolean value) {
        this.useLegacyCS = value;
    }

    protected abstract void setDeviceInfo();

    protected abstract void setOs();

    protected abstract void setAppInfo();

    protected abstract void setUserId();

    protected abstract void PopulateConstantValues();

    protected String HashStringSha256(String str) {
        if(str == null) {
            return "";
        }

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

        if(appExt.getExpId() != null || appExt.getUserId() != null) {
            extensions.put("app", appExt);
        }

        return extensions;
    }

    private void scrubPII(Envelope envelope, EventSensitivity... sensitivities) {
        if(sensitivities == null) {
            return;
        }

        int level = getSensitivityLevel(sensitivities);
        if(level == EventSensitivity.None.getCode()) {
            return;
        }

        // We have to copy these objects so the values we change won't be permanently set.
        user userExtensionFromEnvelope = (user)envelope.getExt().get("user");
        user newUserExtension = new user();
        newUserExtension.setLocalId(userExtensionFromEnvelope.getLocalId());
        newUserExtension.setAuthId(userExtensionFromEnvelope.getAuthId());
        newUserExtension.setId(userExtensionFromEnvelope.getId());
        newUserExtension.setVer(userExtensionFromEnvelope.getVer());
        envelope.getExt().put("user", newUserExtension);

        device deviceExtensionFromEnvelope = (device)envelope.getExt().get("device");
        device newDeviceExtension = new device();
        newDeviceExtension.setLocalId(deviceExtensionFromEnvelope.getLocalId());
        newDeviceExtension.setVer(deviceExtensionFromEnvelope.getVer());
        newDeviceExtension.setId(deviceExtensionFromEnvelope.getId());
        newDeviceExtension.setAuthId(deviceExtensionFromEnvelope.getAuthId());
        newDeviceExtension.setAuthSecId(deviceExtensionFromEnvelope.getAuthSecId());
        newDeviceExtension.setDeviceClass(deviceExtensionFromEnvelope.getDeviceClass());
        envelope.getExt().put("device", newDeviceExtension);

        // The app extension may not always be present if neither experimentId or userId are set
        if(envelope.getExt().containsKey("app")) {
            app appExtensionFromEnvelope = (app) envelope.getExt().get("app");
            app newAppExtension = new app();
            newAppExtension.setExpId(appExtensionFromEnvelope.getExpId());
            newAppExtension.setUserId(appExtensionFromEnvelope.getUserId());
            envelope.getExt().put("app", newAppExtension);
        }

        if(level == EventSensitivity.Drop.getCode()) {
            // Drop PII
            ((user)envelope.getExt().get("user")).setLocalId(null);
            ((device)envelope.getExt().get("device")).setLocalId("r:" + String.valueOf(random.nextLong()));
            // The app extension may not always be present if neither experimentId or userId are set
            if(envelope.getExt().containsKey("app")) {
                ((app)envelope.getExt().get("app")).setUserId(null);
            }


            // Only drop cV if it exists.
            if(correlationVector.isInitialized) {
                envelope.setCV(null);
            }

            envelope.setEpoch(null);
            envelope.setSeqNum(0);
        } else if(level == EventSensitivity.Hash.getCode()) {
            // Hash PII
            ((user)envelope.getExt().get("user")).setLocalId("d:" + HashStringSha256(((user) envelope.getExt().get("user")).getLocalId()));
            ((device)envelope.getExt().get("device")).setLocalId("d:" + HashStringSha256(((device) envelope.getExt().get("device")).getLocalId()));
            // The app extension may not always be present if neither experimentId or userId are set
            if(envelope.getExt().containsKey("app")) {
                ((app)envelope.getExt().get("app")).setUserId("d:" + HashStringSha256(((app) envelope.getExt().get("app")).getUserId()));
            }

            // Only hash cV if it exists.
            if(correlationVector.isInitialized) {
                envelope.setCV(HashStringSha256(envelope.getCV()));
            }

            envelope.setEpoch(HashStringSha256(envelope.getEpoch()));
        }
    }

    private int getSensitivityLevel(EventSensitivity... sensitivities) {
        int level = EventSensitivity.None.getCode();
        for(EventSensitivity sensitivity : sensitivities) {
            if(sensitivity == EventSensitivity.Drop) {
                level = EventSensitivity.Drop.getCode();
                break;
            }

            if(sensitivity == EventSensitivity.Hash) {
                level = EventSensitivity.Hash.getCode();
                continue;
            }
        }

        return level;
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
    private long setFlags(Cll.EventPersistence persistence, Cll.EventLatency latency, Base event, EventSensitivity... sensitivities) {
        flags = 0;

        // Set PII that was passed in through bond file
        String userSensitivity = event.Attributes.get("Sensitivity");
        if(userSensitivity != null && String.valueOf(userSensitivity).compareToIgnoreCase("UserSensitive") == 0) {
            flags |= EventSensitivity.Mark.getCode();
        }

        // Set PII that was passed in by param
        if(sensitivities != null) {
            for (EventSensitivity sensitivity : sensitivities) {
                flags |= sensitivity.getCode();
            }
        }

        // Set Latency
        flags |= latency.getCode();

        // Set persistence
        flags |= persistence.getCode();

        return flags;
    }

    /**
     * Sets the sequence for this event
     */
    private long setSeq(EventSensitivity... sensitivities) {
        // If the event sensitivity contains DROP we don't want
        // to increment the counter, if we did it would look like
        // we are dropping events because this sequence number
        // will be missing.
        if(sensitivities != null) {
            for(EventSensitivity sensitivity : sensitivities) {
                if(sensitivity == EventSensitivity.Drop) {
                    return 0;
                }
            }
        }

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