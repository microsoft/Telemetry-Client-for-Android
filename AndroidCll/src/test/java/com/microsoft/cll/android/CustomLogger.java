package com.microsoft.cll.android;

public class CustomLogger implements ILogger {
    private Verbosity verbosity;

    public CustomLogger() {
        setVerbosity(Verbosity.NONE);
    }

    /*
    Sets the verbosity level. Any thing at or above the set verbosity will be written to log
     */
    @Override
    public void setVerbosity(Verbosity verbosity) {
        this.verbosity = verbosity;
    }

    /*
    Log information messages
     */
    @Override
    public void info(String TAG, String message) {
        if(verbosity == Verbosity.INFO) {
            System.out.println(TAG + ":\t" + message);
        }
    }

    /*
    Log debug messages
     */
    @Override
    public void warn(String TAG, String message) {
        if(verbosity == Verbosity.WARN || verbosity == Verbosity.INFO) {
            System.out.println(TAG + ":\t" + message);
        }
    }

    /*
    Log error messages
     */
    @Override
    public void error(String TAG, String message) {
        if(verbosity == Verbosity.ERROR || verbosity == Verbosity.WARN || verbosity == Verbosity.INFO) {
            System.out.println(TAG + ":\t" + message);
        }
    }
}
