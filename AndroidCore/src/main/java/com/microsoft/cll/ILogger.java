package com.microsoft.cll;

/**
 * A logging interface
 */
public interface ILogger {

    public void setVerbosity(Verbosity verbosity);

    public void info(String TAG, String message);

    public void warn(String TAG, String message);

    public void error(String TAG, String message);

    public enum Verbosity {
        INFO,
        WARN,
        ERROR,
        NONE
    }
}
