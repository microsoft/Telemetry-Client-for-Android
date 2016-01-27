package com.microsoft.cll.android.Overrides;

import com.microsoft.cll.android.ClientTelemetry;
import com.microsoft.cll.android.CustomLogger;
import com.microsoft.cll.android.EventSender;
import com.microsoft.cll.android.Helpers.VortexResponseHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class EventSenderOverride extends EventSender {
    private int sendAttempts = 0;
    private int numberOfEventsAccepted = 0;
    private int numberOfEventsRejected = 0;
    private HttpURLConnection connection;
    public EventSenderOverride(URL url) {
        super(url, new ClientTelemetry(), new CustomLogger());
    }
    public boolean disableNetwork = false;

    @Override
    protected String getResponseBody(BufferedReader reader) {
        String result = "";
        try {
            result = super.getResponseBody(reader);
        }catch (Exception e) {

        }
        numberOfEventsAccepted = VortexResponseHelper.getNumberOfAcceptedEvents(result);
        numberOfEventsRejected = VortexResponseHelper.getNumberOfRejectedEvents(result);
        return result;
    }

    @Override
    protected HttpURLConnection openConnection(final int length, boolean compressed) throws IOException{
        sendAttempts++;
        if(disableNetwork) {
            throw new IOException();
        }

        connection = super.openConnection(length, compressed);
        return connection;
    }

    public int getNumberOfEventsAccepted() {
        return numberOfEventsAccepted;
    }

    public int getNumberOfEventsRejected() {
        return numberOfEventsRejected;
    }

    public int getNumberOfSendAttempts() {
        return sendAttempts;
    }

    public String getHeader(String headerName) {
        return connection.getRequestProperty(headerName);
    }
}
