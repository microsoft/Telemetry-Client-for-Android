package com.microsoft.cll.android;

import com.microsoft.cll.android.Helpers.EventHelper;

import org.junit.Before;
import org.junit.Test;

import java.util.zip.Inflater;

import static junit.framework.TestCase.fail;

public class EventCompressorTests {
    private EventCompressor eventCompressor;
    private Inflater inflater;

    private String actualString;
    private String expectedString;
    private int actualSize;
    private int expectedSize;

    @Before
    public void setup() {
        eventCompressor = new EventCompressor(new CustomLogger());
        inflater = new Inflater(true);
    }

    @Test
    public void compress1Event() {
        generateString(1);
        byte[] bytes = eventCompressor.compress(expectedString);
        inflateBytes(bytes, expectedSize);
        assert(actualString.equals(expectedString));
    }

    @Test
    public void compress100Event() {
        generateString(100);
        byte[] bytes = eventCompressor.compress(expectedString);
        inflateBytes(bytes, expectedSize);
        assert(actualString.equals(expectedString));
    }

    /**
     * Vortex supports a max upload size of 64KB so let's compress large events that add up to more than 64KB when
     *    compressed but are under the 500 event limit
     */
    @Test
    public void compressLargeEvents() {
        String largeEventString = "{\"ver\":1,\"name\":\"Microsoft.Example.HelloWorldBC\",\"time\":\"2014-12-15T17:44:06.0000231Z\",\"sampleRate\":10.0,\"seq\":\"-5388374316054856560:4\",\"iKey\":\"ASM-AndroidHW\",\"flags\":17,\"deviceId\":\"47D9A290B04EE14EC33DFE1EF84315A8FAE7BB836465C4C814F8ACB5AEDA9F0A\",\"os\":\"Android\",\"osVer\":\"4.4.3\",\"appId\":\"com.microsoft.helloworld\",\"appVer\":\"1.0\",\"userId\":\"FE5948CF51113A33D7B3F20E1AD0C88AE457B03B68B8A4D36A159B7203F57C68\",\"tags\":{\"cllVersion\":\"BUILD_LABEL_NOT_SET\",\"cllName\":\"com.microsoft.cll\",\"cV\":\"1ZOrgqtJCJVN89aQ.1\"},\"data\":{\"baseType\":\"Ms.Media.MediaUsage\",\"baseData\":{\"mediaType\":\"Video\",\"lengthMs\":10,\"action\":1,\"positionMs\":0},\"EmailAlias\":\"foobardfgjhkljfjhaslkfjhsdlkgnmjhgkdfjhgfkldjghkhjfkjlksdjfksdjflojassnkfjuddlfwe90238rufwnvjksd7039r089214qe3wu2q34q9r0342emjklifred980sdfajikol3qr980214qjknfdggsdhkdhfkjdhfjsdhfjkhskf239085wfkj09382ir90834329ie49ielirfe89er3fjikw4efrt890p5r34ioj45r3wefijksfewr890r34jkmlir3e90dvfszcijsdfzcx90xji34r980pfamknjl3qwe903re890p342qeuij3qer4jklimf9fijkol3r980jo9n93j9032vu0p3298uv0239q8jp908rwjklaru908i2040dfgdfkjglejtgsrujioulyriursr9kj84qfj\",\"HelloWorldMessage\":\"some extra message information\",\"HelloWorldRating\":5,\"HelloWorldFeedback\":\"instrumenting is pretty easy\"}}";
        generateString(10000, largeEventString);
        byte[] bytes = eventCompressor.compress(expectedString);
        assert(bytes == null);
    }

    private void generateString(int numberOfEvents) {
        generateString(numberOfEvents, EventHelper.singleGoodJsonEvent);
    }

    private void generateString(int numberOfEvents, String event) {
        expectedSize = (event.length()+2) * numberOfEvents;
        EventBatcher batcher = new EventBatcher(expectedSize);
        for( int i = 0; i < numberOfEvents; i++) {
            // Batcher won't support more than 500 events at a time
            if(i % 500 == 0) {
                expectedString += batcher.getBatchedEvents();
                expectedSize = expectedString.length();
            }

            try {
                batcher.addEventToBatch(event);
            } catch (Exception e) {
                fail("Could not add all events to batch");
            }
        }

        expectedString += batcher.getBatchedEvents();
        expectedSize = expectedString.length();
    }

    private void inflateBytes(byte[] bytes, int size) {
        try {
            inflater.setInput(bytes);
            byte[] output = new byte[size]; //Going over what we should expect so if something goes ary we can catch it
            actualSize = inflater.inflate(output);
            inflater.end();
            actualString = new String(output, 0, actualSize, "UTF-8");
        } catch (Exception e) {}

    }
}
