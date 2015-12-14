package com.microsoft.cll.android;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.Locale;

/**
 * Captures Android Specific Part A Information
 */
public class AndroidPartA extends PartA {
    protected final String TAG = "AndroidPartA";
    private final String DeviceTypePhone = "Android.Phone";
    private final String DeviceTypePC = "Android.PC";
    protected Context appContext;


    /**
     * Set variables that will be used across all Part A's and constant
     *
     * @param logger
     */
    public AndroidPartA(ILogger logger, String iKey, Context context, CorrelationVector correlationVector) {
        super(logger, iKey, correlationVector);
        this.appContext = context;
        PopulateConstantValues();
    }

    /**
     * Sets the users id using the google account on the device if present.
     */
    @Override
    protected void setUserId() {
        // Check to see if device has associated google account. If it does we use the first one.
        if(appContext != null) {
            try {
                AccountManager manager = android.accounts.AccountManager.get(appContext);
                Account[] accounts = manager.getAccountsByType("com.google");
                if (accounts.length > 0) {
                    String hashedEmail = HashStringSha256(accounts[0].name);
                    userExt.setLocalId("g:" + hashedEmail);
                    return;
                }
            } catch (SecurityException e) {
                logger.info(TAG, "Get_Accounts permission was not provided. UserID will be blank");
            }
        }

        // If there is not a google account we just use an empty string
        userExt.setLocalId("");
    }

    /**
     * Sets the os to Android
     */
    @Override
    protected void setOs() {
        osName = "Android";
    }

    /**
     * Sets the device unique id using a hash of either the device id if present or the mac address.
     * Also sets the os version and the locale
     */
    @Override
    protected void setDeviceInfo() {
        deviceExt.setLocalId("");
        try {
            if (appContext != null) {
                if (uniqueId == null) {
                    uniqueId = Settings.Secure.getString(
                            appContext.getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    if (uniqueId == null) {
                        WifiManager manager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
                        WifiInfo info = manager.getConnectionInfo();
                        uniqueId = info.getMacAddress().replace(":", "");
                        deviceExt.setLocalId("m:" + uniqueId);
                    } else {
                        deviceExt.setLocalId("a:" + uniqueId);
                    }
                }
            }
        } catch (SecurityException e) {
            logger.info(TAG, "Access Wifi State permission was not Provided. DeviceID will be blank");
        }

        if(testRadioVersion()) {
            deviceExt.setDeviceClass(DeviceTypePhone);
        } else {
            DisplayMetrics dm = new DisplayMetrics();
            ((WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
            int width=dm.widthPixels;
            int height=dm.heightPixels;
            int density=dm.densityDpi;

            double screenInches = getDeviceScreenSize(height, width, density);

            if(screenInches >= 8.0) {
                deviceExt.setDeviceClass(DeviceTypePC);
            } else {
                deviceExt.setDeviceClass(DeviceTypePhone);
            }
        }

        osVer = String.format("%s", android.os.Build.VERSION.RELEASE);
        osExt.setLocale(Locale.getDefault().toString().replaceAll("_", "-"));
    }

    /**
     * Tests the radio version to see if it is null
     * @return False if null or sdk version is < 14
     */
    @TargetApi(14)
    private boolean testRadioVersion() {
        if(Build.VERSION.SDK_INT >= 14) {
            return (android.os.Build.getRadioVersion() != null);
        } else {
            return false;
        }
    }

    /**
     * Sets the apps version information and package name
     */
    @Override
    protected void setAppInfo() {
        final PackageManager manager = appContext.getPackageManager();
        try
        {
            final PackageInfo info = manager.getPackageInfo(
                    appContext.getPackageName(), 0);
            appVer = info.versionName;
            appId = info.packageName;
        }
        catch (final PackageManager.NameNotFoundException e)
        {
            logger.error(TAG, "Could not get package name");
        }
    }

    /**
     * Gets the screen size based off of
     * @param height Screen height in pixels
     * @param width Screen width in pixels
     * @param density Screen density
     * @return The screen size in inches
     */
    double getDeviceScreenSize(int height, int width, int density) {
        double wi=(double)width/(double)density;
        double hi=(double)height/(double)density;
        double x = Math.pow(wi,2);
        double y = Math.pow(hi,2);
        double screenInches = Math.sqrt(x+y);
        return screenInches;
    }

    /**
     * Populate constant values once so we don't have to for every event
     */
    @Override
    protected void PopulateConstantValues()
    {
        setDeviceInfo();
        setUserId();
        setAppInfo();
        setOs();
    }
}
