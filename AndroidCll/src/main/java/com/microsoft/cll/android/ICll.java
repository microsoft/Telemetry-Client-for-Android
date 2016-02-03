package com.microsoft.cll.android;

import com.microsoft.telemetry.Base;

import java.util.Map;

public interface ICll {
    /**
     * Starts the queue-draining background thread and uploader. Start must be called prior
     * to logging events in order to start the queue-draining background thread
     * and uploader.
     */
    void start();


    /**
     * Stops the background thread and uploader.
     */
    void stop();


    /**
     * Puts the Cll in a paused state, allowing it to accept events but not
     * upload until it is resumed.
     */
    void pause();


    /**
     * Resume the Cll from a paused state, allowing uploads to resume.
     * During resume an upload will automatically be triggered.
     */
    void resume();


    /**
     * Allow the host application to set the verbosity to help with debugging during runtime
     * @param verbosity - The verbosity to use
     */
    void setDebugVerbosity(Verbosity verbosity);

    /**
     * Log a bond event
     *
     * @param event
     *            The serializable event to log
     */
    void log(final PreSerializedEvent event, EventSensitivity... sensitivities);

    /**
     * Log a bond event
     *
     * @param event
     *            The serializable event to log
     */
    void log(final Base event, EventSensitivity... sensitivities);

    void log(final Base event, Map<String, String> tags);

    /**
     * Uploads all events in the queue
     */
    void send();

    /**
     * Sets the URL used to send events to
     * setEndpointUrl must be called before events can be sent
     *
     * @param url
     *            Url, including protocol and port
     */
    void setEndpointUrl(final String url);

    /**
     * Sets whether we should use the legacy part A fields or not.
     * @param value True if we should, false if we should not
     */
    void useLegacyCS(boolean value);

    /**
     * Sets the experiment id
     * @param id
     *           The experiment id
     */
    void setExperimentId(String id);

    void synchronize();

    void SubscribeCllEvents(ICllEvents cllEvents);

    /**
     * Sets the userId field in the app extension.
     * @param userId The user id to use
     *               Must start with  'c:' 'i:' or 'w:'
     */
    void setAppUserId(String userId);

    /**
     * @return The userId set in the app extension.
     */
    String getAppUserId();
}
