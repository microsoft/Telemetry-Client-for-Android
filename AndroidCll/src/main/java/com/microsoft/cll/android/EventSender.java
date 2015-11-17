package com.microsoft.cll.android;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * This class handles sending events to Vortex
 */
public class EventSender {
    private final String NO_HTTPS_CONN = "URL didn't return HttpsUrlConnection instance.";
    private final String TAG = "EventSender";
    private final URL endpoint;
    private final ClientTelemetry clientTelemetry;
    private final ILogger logger;

    public EventSender(URL endpoint, ClientTelemetry clientTelemetry, ILogger logger)
    {
        this.endpoint           = endpoint;
        this.clientTelemetry    = clientTelemetry;
        this.logger             = logger;
    }

    /**
     * Sends the events in the body to Vortex
     * * @param body The body to send
     */
    public void sendEvent(String body) throws IOException
    {
        final byte[] bodyBytes = body.getBytes(Charset.forName("UTF-8"));
        sendEvent(bodyBytes, false);
    }

    /**
     * Sends the events in the body to Vortex
     * @param body The body to send
     * @param compressed Whether the body is compressed or not so we can set the appropriate headers
     * @throws IOException An exception is we cannot connect to Vortex
     */
    public void sendEvent(byte[] body, boolean compressed) throws IOException
    {
        final HttpURLConnection connection;
        long start;
        long diff;
        BufferedReader reader;

        clientTelemetry.IncrementVortexHttpAttempts();
        connection = this.openConnection(body.length, compressed);

        try {
            OutputStream stream = connection.getOutputStream();
            stream.write(body);
            stream.flush();
            stream.close();
        } catch (Exception e) {
            logger.error(TAG, "Error writing data");
        }

        start = getTime();
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            diff = getTime() - start;
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            getResponseBody(reader);
            connection.getInputStream().close();

        } else {
            diff = getTime() - start;
            logger.error(TAG, "Bad Response Code");
            clientTelemetry.IncrementVortexHttpFailures(connection.getResponseCode());
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            getResponseBody(reader);
            connection.getErrorStream().close();
        }

        clientTelemetry.SetAvgVortexLatencyMs((int) diff); // ~25 days worth of ms can be stored in an int
        clientTelemetry.SetMaxVortexLatencyMs((int) diff);
    }

    /**
     * Opens a URLConnection to the endpoint. Caller is responsible for closing
     * when finished.
     *
     * @return HttpURLConnection to the resource.
     * @throws IOException
     *             Thrown if connection returned is not an HttpURLConnection
     */
    protected HttpURLConnection openConnection(final int length, boolean compressed) throws IOException
    {
        final URLConnection connection = this.endpoint.openConnection();
        if (connection instanceof HttpURLConnection)
        {
            final HttpURLConnection httpsConnection = (HttpURLConnection) connection;
            httpsConnection.setInstanceFollowRedirects(false);
            httpsConnection.setRequestMethod("POST");
            httpsConnection.setUseCaches(false);
            httpsConnection.setRequestProperty("Content-Type", "application/x-json-stream; charset=utf-8");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            httpsConnection.setRequestProperty("X-UploadTime", dateFormat.format(new Date()).toString());
            httpsConnection.setRequestProperty("Content-Length",Integer.toString(length));
            httpsConnection.setConnectTimeout(SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.HTTPTIMEOUTINTERVAL));
            httpsConnection.setDoOutput(true);

            if(compressed) {
                httpsConnection.setRequestProperty("Accept", "application/json");
                httpsConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
                httpsConnection.setRequestProperty("Content-Encoding", "deflate");
            }

            httpsConnection.connect();
            return httpsConnection;
        }
        else
        {
            clientTelemetry.IncrementVortexHttpFailures(-1);
            throw new IOException(NO_HTTPS_CONN);
        }
    }

    /**
     * Reads the response from the http connection
     * @param reader A reader that is attached to the response body
     */
    protected String getResponseBody(BufferedReader reader)
    {
        final StringBuilder responseBuilder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
        } catch(IOException e) {
            logger.error(TAG, "Couldn't read response body");
        }

        // Check to see if any events were rejected
        try {
            JSONObject jsonObject = new JSONObject(responseBuilder.toString());
            int rejectCount = jsonObject.getInt("rej");
            clientTelemetry.IncremenetRejectDropCount(rejectCount);
        } catch (JSONException e) {
            logger.info(TAG, e.getMessage());
        } catch (RuntimeException e) {
            logger.info(TAG, e.getMessage());
        }

        logger.info(TAG, responseBuilder.toString());
        return responseBuilder.toString();
    }

    /**
     * Gets the current time in milliseconds for UTC
     * @return The time in milliseconds
     */
    private long getTime()
    {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US).getTimeInMillis();
    }
}
