package com.microsoft.cll.Helpers;

import com.microsoft.telemetry.Envelope;
import com.microsoft.telemetry.Extension;
import com.microsoft.telemetry.extensions.device;

public class PartAHelper {
    public static String getDeviceId(Envelope event) {
        device deviceExt = (device)event.getExt().get("device");
        return deviceExt.getLocalId();
    }

    public static void setDeviceId(Envelope event, String value) {
        device deviceExt = (device) event.getExt().get("device");
        deviceExt.setLocalId(value);
        event.getExt().put("device", deviceExt);
    }
}