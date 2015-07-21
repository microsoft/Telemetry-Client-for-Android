package com.microsoft.cll;

import android.util.Log;

/**
 * Handles android logging using logcat
 */
public class AndroidLogger implements ILogger {
    private Verbosity verbosity;

    private static AndroidLogger INSTANCE;
    private static Object InstanceLock = new Object();

    public static ILogger getInstance() {
        if(INSTANCE == null) {
            synchronized (InstanceLock) {
                if(INSTANCE == null) {
                    INSTANCE = new AndroidLogger();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Create an android logger with the default verbosity of none
     */
    private AndroidLogger() {
        setVerbosity(Verbosity.NONE);
    }

    /**
     * Sets the verbosity level. Any thing at or above the set verbosity will be written to log
     * @param verbosity The verbosity level to use
     */
    @Override
    public void setVerbosity(Verbosity verbosity) {
        this.verbosity = verbosity;
    }

    /**
     * Log information messages
     * @param TAG The Tag to log with
     * @param message The message to log
     */
    @Override
    public void info(String TAG, String message) {
        if(verbosity == Verbosity.INFO) {
            Log.i(TAG, message);
        }
    }

    /**
     * Log debug messages
     * @param TAG The Tag to log with
     * @param message The message to log
     */
    @Override
    public void warn(String TAG, String message) {
        if(verbosity == Verbosity.WARN || verbosity == Verbosity.INFO) {
            Log.d(TAG, message);
        }
    }

    /**
     * Log error messages
     * @param TAG The Tag to log with
     * @param message The message to log
     */
    @Override
    public void error(String TAG, String message) {
        if(verbosity == Verbosity.ERROR || verbosity == Verbosity.WARN || verbosity == Verbosity.INFO) {
            Log.e(TAG, message);
        }
    }
}
