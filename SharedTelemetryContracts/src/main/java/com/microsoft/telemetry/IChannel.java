package com.microsoft.telemetry;

public interface IChannel {
    void log(final Base telemetry);
    void synchronize();
}
