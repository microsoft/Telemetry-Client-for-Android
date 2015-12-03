package com.microsoft.cll.android;

import android.test.AndroidTestCase;

import com.microsoft.telemetry.Base;

public class AndroidPartATests extends AndroidTestCase {

    // Tests the screen size conversion call
    public void testDeviceScreenSizeConversion() {
        AndroidPartA partA = new AndroidPartA(AndroidLogger.getInstance(), "", this.getContext(), new CorrelationVector());
        double size = partA.getDeviceScreenSize(1776, 1080, 480);
        assertTrue(size < 4.34 && size > 4.32);
    }

    // Regression test for null Sensitivity list that before resulted in a NullPointerException.
    public void testScrubNullPII() {
        AndroidPartA partA = new AndroidPartA(AndroidLogger.getInstance(), "", this.getContext(), new CorrelationVector());
        Base base = new Base();
        base.QualifiedName = "com.microsoft.test";
        partA.populate(base, null, null);
    }
}
