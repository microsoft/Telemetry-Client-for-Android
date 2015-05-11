package com.microsoft.cll.Android;

import android.test.AndroidTestCase;
import com.microsoft.cll.AndroidLogger;

public class AndroidPartATests extends AndroidTestCase {

    // Tests the screen size conversion call
    public void testDeviceScreenSizeConversion() {
        AndroidPartA partA = new AndroidPartA(new AndroidLogger(), "", this.getContext());
        double size = partA.getDeviceScreenSize(1776, 1080, 480);
        assertTrue(size < 4.34 && size > 4.32);
    }
}
