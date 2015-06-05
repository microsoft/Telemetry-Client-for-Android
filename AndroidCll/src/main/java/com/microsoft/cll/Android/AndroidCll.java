package com.microsoft.cll.Android;

import android.content.Context;
import android.content.SharedPreferences;

import com.microsoft.cll.AndroidLogger;
import com.microsoft.cll.Cll;
import com.microsoft.cll.CllEvents;
import com.microsoft.cll.EventHandler;
import com.microsoft.cll.ICllEvents;
import com.microsoft.cll.SettingsStore;
import com.microsoft.telemetry.IChannel;

import java.util.Map;

/**
 * This is the main class used for logging events.
 */
public class AndroidCll extends Cll implements SettingsStore.UpdateListener{
    private static final String cllName = "AndroidCLL";
    private final String sharedPreferencesName = "AndroidCllSharedPreferences";
    private final SharedPreferences preferences;

    /**
     * Create a Cll for Android
     * @param app The application context
     */
    public AndroidCll(Context app)
    {
        this("", app);
    }

    /**
     * Create a Cll for Android
     * @param iKey Your iKey
     * @param context The application context
     */
    public AndroidCll(String iKey, Context context) {
        super(iKey, new AndroidLogger(), cllName);
        this.partA               = new AndroidPartA(logger, iKey, context);
        this.eventHandler        = new EventHandler(clientTelemetry, cllEvents, logger, context.getFilesDir().getPath().toString());

        this.cllEvents.add(new CllEvents(this.partA, this.clientTelemetry, this));
        this.preferences = context.getSharedPreferences(sharedPreferencesName, 0);

        setSettingsStoreValues();
        SettingsStore.setUpdateListener(this);
    }

    /**
     * Subscribe to cll events
     * @param cllEvents your cll event object which we will callback on when an event occurs
     */
    public void SubscribeCllEvents(ICllEvents cllEvents) {
        this.cllEvents.add(cllEvents);
    }

    @Override
    public void OnUpdate(String settingName, String settingValue) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    private void setSettingsStoreValues() {
        Map<String, String> settings = (Map<String, String>) preferences.getAll();
        for(Map.Entry<String, String> setting : settings.entrySet()) {
            SettingsStore.updateAppSetting(setting.getKey(), setting.getValue());
        }
    }

    public static IChannel initialize(String iKey, Context app, String endpoint) {
        AndroidCll cll = new AndroidCll(iKey, app);
        cll.setEndpointUrl(endpoint);
        cll.start();
        return cll;
    }
}
