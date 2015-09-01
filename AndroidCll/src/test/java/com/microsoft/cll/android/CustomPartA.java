package com.microsoft.cll.android;

import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Locale;

public class CustomPartA extends PartA {
    protected final String TAG = "CustomPartA";

    public CustomPartA(ILogger logger, String iKey) {
        super(logger, iKey);
        PopulateConstantValues();
    }

    @Override
    protected void setAppInfo()
    {
        // TODO: Populate app info in generic java
        appId = "appId";
        appVer = "1.1.1.1";
    }

    @Override
    protected void setDeviceInfo()
    {
        osExt.setLocale(Locale.getDefault().getDisplayName());
        deviceExt.setLocalId("h:" + HashStringSha256(getMacAddress()));
    }

    @Override
    protected void setOs()
    {
        osName = System.getProperty("os.name");
        osVer = System.getProperty("os.version");
    }

    @Override
    protected void setUserId()
    {
        userExt.setLocalId(System.getProperty("user.name"));
    }

    @Override
    protected void PopulateConstantValues()
    {
        setDeviceInfo();
        setUserId();
        setOs();
        setAppInfo();
    }

    protected String getMacAddress()
    {
        byte[] mac = null;
        try {
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            while(networks.hasMoreElements()) {
                NetworkInterface network = networks.nextElement();
                mac = network.getHardwareAddress();

                if(mac == null) {
                    continue;
                }

                break;
            }
        } catch (Exception e) {
            logger.error(TAG, "Could not get MAC address");
            return "";
        }

        if(mac == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++)
        {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }

        return sb.toString();
    }

    protected void setAppId(String appId) {
        this.appId = appId;
        if(appId != null && appVer != null) {
            populateAppId();
        }
    }

    protected void setAppVersion(String appVersion) {
        this.appVer = appVersion;

        if(appId != null && appVer != null) {
            populateAppId();
        }
    }

    /**
     * We have to wait till we have both appId and appVer to populate the true app id.
     */
    private void populateAppId() {
        String fullAppId = String.format("J:%s_%s_%s", appId, appVer, System.getProperty("os.arch"));
        this.appId = fullAppId;
    }

}
