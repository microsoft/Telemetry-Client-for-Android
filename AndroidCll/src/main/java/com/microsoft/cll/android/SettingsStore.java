package com.microsoft.cll.android;

import com.microsoft.telemetry.Base;

import java.util.HashMap;

/**
 * This is a static class for managing the values we get back from OneSettings
 */
public class SettingsStore {
    private static HashMap<String, String> hostEventSettings = new HashMap<String, String>();
    protected static HashMap<Settings, Object> cllSettings = new HashMap<Settings, Object>();
    private static UpdateListener updateListener;
    public enum Settings {
        SYNCREFRESHINTERVAL,
        QUEUEDRAININTERVAL,
        SNAPSHOTSCHEDULEINTERVAL,
        MAXEVENTSIZEINBYTES,
        MAXEVENTSPERPOST,
        SAMPLERATE,
        MAXFILESSPACE,
        UPLOADENABLED,
        PERSISTENCE,
        LATENCY,
        HTTPTIMEOUTINTERVAL,
        THREADSTOUSEWITHEXECUTOR,
        MAXCORRELATIONVECTORLENGTH,
        MAXCRITICALCANADDATTEMPTS,
        MAXRETRYPERIOD,
        BASERETRYPERIOD,
        CONSTANTFORRETRYPERIOD,
        NORMALEVENTMEMORYQUEUESIZE,
        CLLSETTINGSURL,
        HOSTSETTINGSETAG,
        CLLSETTINGSETAG,
        VORTEXPRODURL
    }

    static {
        cllSettings.put(Settings.SYNCREFRESHINTERVAL, 30 * 60);                     // Interval in seconds that we sync settings
        cllSettings.put(Settings.QUEUEDRAININTERVAL, 10);                           // Interval in seconds that we empty the queue
        cllSettings.put(Settings.SNAPSHOTSCHEDULEINTERVAL, 10 * 60);                // Interval in seconds that we send the snapshot
        cllSettings.put(Settings.MAXEVENTSIZEINBYTES, 64000);                       // Limit of post size in bytes
        cllSettings.put(Settings.MAXEVENTSPERPOST, 500);                            // Max events supported per post
        cllSettings.put(Settings.SAMPLERATE, 10);                                   // Sample Rate is a percentage
        cllSettings.put(Settings.MAXFILESSPACE, 50 * 1024 * 1024);                  // This is the maximum amount of storage space we will use for files
        cllSettings.put(Settings.PERSISTENCE, Cll.EventPersistence.NORMAL);         // The default persistence for events
        cllSettings.put(Settings.LATENCY, Cll.EventLatency.NORMAL);                 // The default latency for events
        cllSettings.put(Settings.UPLOADENABLED, true);                              // Master control to turn off event upload in case of emergency
        cllSettings.put(Settings.HTTPTIMEOUTINTERVAL, 5000);
        cllSettings.put(Settings.THREADSTOUSEWITHEXECUTOR, 3);
        cllSettings.put(Settings.MAXCORRELATIONVECTORLENGTH, 63);
        cllSettings.put(Settings.MAXCRITICALCANADDATTEMPTS, 5);
        cllSettings.put(Settings.MAXRETRYPERIOD, 60);
        cllSettings.put(Settings.BASERETRYPERIOD, 2);
        cllSettings.put(Settings.CONSTANTFORRETRYPERIOD, 3);
        cllSettings.put(Settings.NORMALEVENTMEMORYQUEUESIZE, 10);
        cllSettings.put(Settings.CLLSETTINGSURL, "https://settings.data.microsoft.com/settings/v2.0/androidLL/app");
        cllSettings.put(Settings.HOSTSETTINGSETAG, "");
        cllSettings.put(Settings.CLLSETTINGSETAG, "");
        cllSettings.put(Settings.VORTEXPRODURL, "https://vortex.data.microsoft.com/collect/v1");
    }

    protected static int getCllSettingsAsInt(Settings setting) {
        return Integer.parseInt(cllSettings.get(setting).toString());
    }

    protected static long getCllSettingsAsLong(Settings setting) {
        return Long.parseLong(cllSettings.get(setting).toString());
    }

    protected static boolean getCllSettingsAsBoolean(Settings setting) {
        return Boolean.parseBoolean(cllSettings.get(setting).toString());
    }

    protected static String getCllSettingsAsString(Settings setting) {
        return cllSettings.get(setting).toString();
    }

    public static void setUpdateListener(UpdateListener updateListener) {
        SettingsStore.updateListener = updateListener;
    }

    public static void updateHostSetting(String settingName, String settingValue) {
        // Only perform the update action if the setting's value isn't present or has changed
        if(hostEventSettings.get(settingName) == null || !hostEventSettings.get(settingName).equals(settingValue)) {
            hostEventSettings.put(settingName, settingValue);
            if(updateListener != null) {
                updateListener.OnHostSettingUpdate(settingName, settingValue);
            }
        }
    }

    public static void updateCllSetting(SettingsStore.Settings settingName, String settingValue) {
        // Only perform the update action if the setting's value has changed
        if(!cllSettings.get(settingName).equals(settingValue)) {
            SettingsStore.cllSettings.put(settingName, settingValue);
            if(updateListener != null) {
                updateListener.OnCllSettingUpdate(settingName.toString(), settingValue);
            }
        }
    }

    /**
     * Gets the requested setting checking for a cloud result first, then file, then default.
     *
     * @param base
     * @param settingName
     * @return
     */
    public static Object getSetting(Base base, SettingsStore.Settings settingName) {
        String qualifiedEventName = base.QualifiedName;
        Object result = GetCloudSetting(settingName, qualifiedEventName);

        if (result == null) {
            result = getEventSchemaSetting(base, settingName.toString());
        }

        if (result == null) {
            result = getDefaultSetting(settingName);
        }

        return result;
    }

    /**
     * Checks for cloud setting
     *
     * @param settingName
     * @param qualifiedEventName
     * @return
     */
    private static Object GetCloudSetting(SettingsStore.Settings settingName, String qualifiedEventName) {
        qualifiedEventName = qualifiedEventName.toUpperCase();
        String namespace = qualifiedEventName.substring(0, qualifiedEventName.lastIndexOf("."));
        String eventName = qualifiedEventName.substring(qualifiedEventName.lastIndexOf(".") + 1);
        if (SettingsStore.hostEventSettings.containsKey(namespace + ":" + eventName + "::" + settingName)) {
            return SettingsStore.hostEventSettings.get(namespace + ":" + eventName + "::" + settingName);
        } else if (SettingsStore.hostEventSettings.containsKey(":" + eventName + "::" + settingName)) {
            return SettingsStore.hostEventSettings.get(":" + eventName + "::" + settingName);
        } else if (SettingsStore.hostEventSettings.containsKey(namespace + ":::" + settingName)) {
            return SettingsStore.hostEventSettings.get(namespace + ":::" + settingName);
        } else if (SettingsStore.hostEventSettings.containsKey(":::" + settingName)) {
            return SettingsStore.hostEventSettings.get(":::" + settingName);
        }

        return null;
    }

    /**
     * Retrieves the setting value from the schema if present
     *
     * @param base        The schema
     * @param settingName The setting to retrieve
     * @return The value of the setting if present, otherwise null
     */
    private static Object getEventSchemaSetting(Base base, String settingName) {
        return base.Attributes.get(settingName);
    }

    /**
     * Checks for default setting
     *
     * @param settingName
     * @return
     */
    public static Object getDefaultSetting(SettingsStore.Settings settingName) {
        return SettingsStore.cllSettings.get(settingName);
    }

    public interface UpdateListener {
        void OnHostSettingUpdate(String settingName, String SettingValue);
        void OnCllSettingUpdate(String settingName, String SettingValue);
    }
}
