package com.microsoft.cll.Android;

import android.content.Context;
import android.content.SharedPreferences;
import com.microsoft.cll.*;
import com.microsoft.cll.AndroidLogger;

import java.util.Map;

/**
 * This is the main class used for logging events.
 */
public class AndroidCll extends Cll implements SettingsStore.UpdateListener{
    private static final String cllName = "AndroidCLL";
    private final String sharedPreferencesName = "AndroidCllSharedPreferences";
    private Context context;

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
        this.context = context;
        this.partA               = new AndroidPartA(logger, iKey, context);
        this.eventHandler        = new EventHandler(clientTelemetry, cllEvents, logger, context.getFilesDir().getPath().toString());

        this.cllEvents.add(new CllEvents(this.partA, this.clientTelemetry, this));

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
        SharedPreferences preferences = context.getSharedPreferences(sharedPreferencesName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    private void setSettingsStoreValues() {
        SharedPreferences preferences = context.getSharedPreferences(sharedPreferencesName, 0);
        Map<String, String> settings = (Map<String, String>) preferences.getAll();
        for(Map.Entry<String, String> setting : settings.entrySet()) {
            SettingsStore.updateAppSetting(setting.getKey(), setting.getValue());
        }
    }
}
