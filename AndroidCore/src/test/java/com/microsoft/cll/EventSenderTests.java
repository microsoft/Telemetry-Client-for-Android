package com.microsoft.cll;
import com.microsoft.cll.Helpers.EventHelper;
import com.microsoft.cll.Overrides.EventSenderOverride;
import org.junit.*;

import java.net.URL;

import static org.junit.Assert.fail;


public class EventSenderTests {
    private EventSenderOverride eventSenderOverride = null;
    @Before
    public void setup() {
        try {
            URL url = new URL("https://vortex.data.microsoft.com/collect/v1");
            eventSenderOverride = new EventSenderOverride(url);
        } catch (Exception e) {
            fail("Failed to setup test");
        }
    }

    @Test
    public void send1GoodEvent() {
        try {
            eventSenderOverride.sendEvent(EventHelper.singleGoodJsonEvent);
        }catch (Exception e) {
        }

        assert(eventSenderOverride.getNumberOfEventsAccepted() == 1);
    }

    @Test
    public void send1BadEvent() {
        try {
            eventSenderOverride.sendEvent(EventHelper.singleBadJsonEvent);
        }catch (Exception e) {
            // An exception will be thrown for the sendEvent call. This is because Vortex returns a 400 response code for a bad event.
            // This is expected so we just ignore it.
        }

        assert(eventSenderOverride.getNumberOfEventsAccepted() == 0 && eventSenderOverride.getNumberOfEventsRejected() == 1);
    }

    @Test
    public void send100GoodEvents() {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            for(int i = 0; i < 100; i++) {
                stringBuilder.append(EventHelper.singleGoodJsonEvent).append("\r\n");
            }

            eventSenderOverride.sendEvent(stringBuilder.toString());
        }catch (Exception e) {
        }

        assert(eventSenderOverride.getNumberOfEventsAccepted() == 100);
    }

    @Test
    public void send1CompressedEvent() {
        try {
            EventCompressor compressor = new EventCompressor(new CustomLogger());
            byte[] output = compressor.compress(EventHelper.singleGoodJsonEvent);
            eventSenderOverride.sendEvent(output, true);
        }catch (Exception e) {
        }

        assert(eventSenderOverride.getNumberOfEventsAccepted() == 1);
    }

    @Test
    public void send100CompressedEvents() {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            for(int i = 0; i < 100; i++) {
                stringBuilder.append(EventHelper.singleGoodJsonEvent).append("\r\n");
            }

            EventCompressor compressor = new EventCompressor(new CustomLogger());
            byte[] output = compressor.compress(stringBuilder.toString());

            eventSenderOverride.sendEvent(output, true);
        }catch (Exception e) {
        }

        assert(eventSenderOverride.getNumberOfEventsAccepted() == 100);
    }

    @Test
    public void sendBadCompressedEvents() {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            for(int i = 0; i < 100; i++) {
                stringBuilder.append(EventHelper.singleBadJsonEvent).append("\r\n");
            }

            EventCompressor compressor = new EventCompressor(new CustomLogger());
            byte[] output = compressor.compress(stringBuilder.toString());

            eventSenderOverride.sendEvent(output, true);
        }catch (Exception e) {
        }

        assert(eventSenderOverride.getNumberOfEventsAccepted() == 0 && eventSenderOverride.getNumberOfEventsRejected() == 100);
    }

    @Test
    public void xUploadTimeHeaderSet() {
        try {
            eventSenderOverride.sendEvent(EventHelper.singleGoodJsonEvent);
        }catch (Exception e) {
        }
        String header = eventSenderOverride.getHeader("X-UploadTime");
        assert(header != null);
    }
}
