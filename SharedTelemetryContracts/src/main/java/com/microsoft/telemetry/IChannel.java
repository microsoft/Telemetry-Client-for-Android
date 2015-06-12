package com.microsoft.telemetry;

import java.util.Map;

public interface IChannel {
    void log(final Base telemetry, Map<String, String> tags);
    void synchronize();
}
