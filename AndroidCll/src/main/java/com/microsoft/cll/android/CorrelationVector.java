package com.microsoft.cll.android;

import java.util.Random;

/**
 * The correlation vector
 */
public class CorrelationVector
{
    private String baseVector;
    private int currentVector;

    private final String base64CharSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private final int id0Length = 16;
    boolean isInitialized = false;

    /**
     * Sets up the vector class with a random base vector and current vector count of 1 and sets initialized to true
     */
    public void Init() {
        baseVector = SeedCorrelationVector();
        currentVector = 1;
        isInitialized = true;
    }

    /**
     * Checks to see if we can add an extra vector
     */
    private boolean CanExtend()
    {
        int vectorSize = (int) Math.floor(Math.log10(currentVector) + 1);

        // Base length + dot + vector length + dot + new vector length (0)
        if(baseVector.length() + 1 + vectorSize + 1 + 1 > SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXCORRELATIONVECTORLENGTH)) {
            return false;
        }

        return true;
    }

    /**
     * Checks to see if we can increment the current vector
     */
    private boolean CanIncrement(int newVector)
    {
        if(newVector - 1 == Integer.MAX_VALUE) {
            return false;
        }
        int vectorSize = (int) Math.floor(Math.log10(newVector) + 1);

        // Get the length of the existing string + length of the new extension + the length of the dot
        if(baseVector.length() + vectorSize + 1 > SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXCORRELATIONVECTORLENGTH)) {
            return false;
        }

        return true;
    }

    /**
     * Adds another vector
     */
    public synchronized String Extend()
    {
        if(!isInitialized) {
            Init();
        }

        if(CanExtend()) {
            baseVector = GetValue();
            currentVector = 1;
        }

        return GetValue();
    }

    /**
     * Returns the Current Correlation Vector
     */
    public String GetValue()
    {
        if(!isInitialized) {
            return null;
        }

        return baseVector + "." + currentVector;
    }

    /**
     * Increments the current vector
     */
    public synchronized String Increment()
    {
        if(!isInitialized) {
            Init();
        }

        int newVector = currentVector + 1;
        // Check if we can increment
        if(CanIncrement(newVector)) {
            currentVector = newVector;
        }

        return GetValue();
    }

    /**
     * Checks to see if the correlation vector is valid
     */
    boolean IsValid(String vector)
    {
        if(vector.length() > SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXCORRELATIONVECTORLENGTH)) {
            return false;
        }

        String validationPattern = "^[" + base64CharSet + "]{16}(.[0-9]+)+$";
        if(!vector.matches(validationPattern)) {
            return false;
        }

        return true;
    }

    /**
     * Randomly generates a string for the base vector
     */
    private String SeedCorrelationVector()
    {
        String result = "";

        Random r = new Random();
        for (int i = 0; i < id0Length; i++)
        {
            result += base64CharSet.charAt(r.nextInt(base64CharSet.length()));
        }

        return result;
    }

    /**
     * Sets the base and current vector values
     */
    public synchronized void SetValue(String vector)
    {
        if(IsValid(vector)) {
            int lastDot = vector.lastIndexOf(".");
            baseVector = vector.substring(0, lastDot);
            currentVector = Integer.parseInt(vector.substring(lastDot + 1));
            isInitialized = true;
        } else {
            throw new IllegalArgumentException("Cannot set invalid correlation vector value");
        }
    }
}