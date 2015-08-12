package com.microsoft.cll.android;

import android.test.AndroidTestCase;

public class AndroidPartATests extends AndroidTestCase {

    // Tests the screen size conversion call
    public void testDeviceScreenSizeConversion() {
        AndroidPartA partA = new AndroidPartA(AndroidLogger.getInstance(), "", this.getContext());
        double size = partA.getDeviceScreenSize(1776, 1080, 480);
        assertTrue(size < 4.34 && size > 4.32);
    }
}
