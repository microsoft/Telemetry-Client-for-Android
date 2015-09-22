package com.microsoft.cll.android;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Settings sync periodically checks the remote settings store to toggle events
 * on/off, change upload rates, change sampling and filtering.
 */
public class SettingsSync extends ScheduledWorker
{

    private final ClientTelemetry clientTelemetry;
    private final ILogger logger;
    private final String TAG = "SettingsSync";
    private final List<AbstractSettings> settingsList;

    public SettingsSync(ClientTelemetry clientTelemetry, ILogger logger, String iKey, PartA partA)
    {
        super(SettingsStore.getCllSettingsAsLong(SettingsStore.Settings.SYNCREFRESHINTERVAL));

        this.clientTelemetry = clientTelemetry;
        this.logger = logger;
        this.settingsList = new ArrayList<AbstractSettings>();
        this.settingsList.add(new CllSettings(clientTelemetry, logger, this, partA));

        // Only add host settings sync if we have an ikey to use to look up host settings
        if(!iKey.equals("")) {
            this.settingsList.add(new HostSettings(clientTelemetry, logger, iKey, partA));
        }
    }

    /**
     * Sync settings. Since it invokes an http connection, this method should
     * never be called from a UI thread! Always call it from a Handler.
     */
    @Override
    public void run()
    {
        logger.info(TAG, "Cloud sync!");
        this.GetCloudSettings();
    }

    /**
     * Makes a connection to the settings endpoint, applies settings to the map
     * containing settings.
     */
    private void GetCloudSettings() {
        for(AbstractSettings abstractSettings : settingsList) {
            JSONObject json = abstractSettings.getSettings();
            if(json == null)
            {
                logger.error(TAG, "Could not get or parse settings");
                continue;
            }

            abstractSettings.ParseSettings(json);
        }
    }
}