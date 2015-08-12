/**
 * Copyright Microsoft Corporation 2014
 * All Rights Reserved
 */

package com.microsoft.cll;

import com.microsoft.telemetry.Base;
import com.microsoft.telemetry.IChannel;

import java.util.Map;

/**
 * The cll main class that should be called into via the client application.
 * The CLL must be initialized first with the <code>start()</code> method, which
 * starts the thread for collection of <code>IJsonSerializable</code> events, and then
 * <code>setEndpointUrl</code> must be called to set the url for the events to be sent to.
 */
public class Cll implements IChannel, ICll
{
    protected ICll cll;

    /**
     * Initializes the CLL with the given provider.
     */
    protected Cll(String iKey, ILogger logger, String cllName, String eventDir, PartA partA)
    {
        cll = SingletonCll.getInstance(iKey, logger, cllName, eventDir, partA);
    }

    @Override
    public void start() {
        cll.start();
    }

    @Override
    public void stop() {
        cll.stop();
    }

    @Override
    public void pause() {
        cll.pause();
    }

    @Override
    public void resume() {
        cll.resume();
    }

    @Override
    public void setDebugVerbosity(Verbosity verbosity) {
        cll.setDebugVerbosity(verbosity);
    }

    @Override
    public void log(PreSerializedEvent event) {
        cll.log(event);
    }

    @Override
    public void log(Base event) {
        cll.log(event);
    }

    @Override
    public void log(Base telemetry, Map<String, String> tags) {
        cll.log(telemetry, tags);
    }

    @Override
    public void send() {
        cll.send();
    }

    @Override
    public void setEndpointUrl(String url) {
        cll.setEndpointUrl(url);
    }

    @Override
    public void useLagacyCS(boolean value) {
        cll.useLagacyCS(value);
    }

    @Override
    public void setExperimentId(String id) {
        cll.setExperimentId(id);
    }

    @Override
    public void synchronize() {
        cll.synchronize();
    }

    @Override
    public void SubscribeCllEvents(ICllEvents cllEvents) {
        cll.SubscribeCllEvents(cllEvents);
    }

    /**
     * enum for the persistence of an event
     */
    public enum EventPersistence
    {
        NORMAL(1),
        CRITICAL(2);

        private int value;

        private EventPersistence(int v) {
            value = v;
        }

        public int getCode() {
            return value;
        }

        public static EventPersistence getPersistence(int value) {
            switch (value) {
                case 1:
                    return NORMAL;
                case 2:
                    return CRITICAL;
            }

            return null;
        }
    }

    /**
     * Enum for the latency of an event
     */
    public enum EventLatency
    {
        NORMAL(1),
        REALTIME(2);

        private int value;

        private EventLatency(int v) {
            value = v;
        }

        public int getCode() {
            return value;
        }

        public static EventLatency getLatency(int value) {
            switch (value) {
                case 1:
                    return NORMAL;
                case 2:
                    return REALTIME;
            }

            return null;
        }
    }
}
