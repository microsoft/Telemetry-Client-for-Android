package com.microsoft.cll.android;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * These are settings specific to the cll itself. This uses the base Settings class for retrieving the settings
 */
public class CllSettings extends AbstractSettings
{

    private final SettingsSync settingsSync;

    public CllSettings(ClientTelemetry clientTelemetry, ILogger logger, SettingsSync settingsSync)
    {
        super(clientTelemetry, logger);

        this.endpoint = SettingsStore.getCllSettingsAsString(SettingsStore.Settings.CLLSETTINGSURL);
        this.settingsSync = settingsSync;
        this.TAG = "CllSettings";
    }

    /**
     *Parses the settings returned from OneSettings
     */
    @Override
    public void ParseSettings(JSONObject resultJson)
    {
        try {
            if (resultJson != null && resultJson.has("settings")) {
                int refreshInterval = resultJson.getInt("refreshInterval") * 60; // Convert from minutes to seconds
                if (refreshInterval != SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.SYNCREFRESHINTERVAL)) {
                    SettingsStore.cllSettings.put(SettingsStore.Settings.SYNCREFRESHINTERVAL, refreshInterval);
                    settingsSync.nextExecution.cancel(false);
                    settingsSync.nextExecution = settingsSync.executor.scheduleAtFixedRate(
                            settingsSync,
                            SettingsStore.getCllSettingsAsLong(SettingsStore.Settings.SYNCREFRESHINTERVAL),
                            SettingsStore.getCllSettingsAsLong(SettingsStore.Settings.SYNCREFRESHINTERVAL),
                            TimeUnit.SECONDS);
                }

                JSONObject jsonSettings = (JSONObject) resultJson.get("settings");
                Iterator<String> keys = jsonSettings.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String value = jsonSettings.getString(key);

                    try {
                        SettingsStore.updateCllSetting(SettingsStore.Settings.valueOf(key), value);
                        logger.info(TAG, "Json Settings, Key: " + key + " Value: " + value);
                    }catch (Exception e) {
                        logger.warn(TAG, "Key: " + key + " was not found");
                    }
                }
            }
        } catch (Exception e) {
            logger.error(TAG, "An exception occurred while parsing settings");
        }
    }
}