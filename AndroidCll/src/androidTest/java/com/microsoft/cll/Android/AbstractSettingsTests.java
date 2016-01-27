package com.microsoft.cll.android;

import android.test.AndroidTestCase;

/**
 * Created by jmorman on 8/31/2015.
 */
public class AbstractSettingsTests extends AndroidTestCase {
        public void testSettingsQueryParameters() {
            String iKey = "myiKey";
            ILogger logger = AndroidLogger.getInstance();
            ClientTelemetry clientTelemetry = new ClientTelemetry();
            PartA partA = new AndroidPartA(logger, iKey, this.getContext(), new CorrelationVector());
            AbstractSettings settings = new HostSettings(clientTelemetry, logger, iKey, partA);
            String result = settings.getQueryParameters();

            StringBuilder expectedResultSB = new StringBuilder();
            expectedResultSB.append("?")
                    .append("os=").append(partA.osName)
                    .append("&osVer=").append(partA.osVer)
                    .append("&deviceClass=").append(partA.deviceExt.getDeviceClass())
                    .append("&deviceId=").append(partA.deviceExt.getLocalId());
            String expectedResult = expectedResultSB.toString();
            assertEquals(expectedResult, result);
        }
}
