package com.microsoft.cll.Helpers;

import com.microsoft.cll.CustomLogger;
import com.microsoft.cll.CustomPartA;
import com.microsoft.telemetry.IJsonSerializable;

import Microsoft.Example.HelloWorldBC;
import Microsoft.Example.HelloWorldC;
import Ms.Media.MediaUsage;

public class EventHelper {
    public static final String singleGoodJsonEvent = "{\"ver\":1,\"name\":\"Microsoft.Example.HelloWorldBC\",\"time\":\"2014-12-15T17:44:06.0000231Z\",\"sampleRate\":10.0,\"seq\":\"-5388374316054856560:4\",\"iKey\":\"ASM-AndroidHW\",\"flags\":17,\"deviceId\":\"47D9A290B04EE14EC33DFE1EF84315A8FAE7BB836465C4C814F8ACB5AEDA9F0A\",\"os\":\"Android\",\"osVer\":\"4.4.3\",\"appId\":\"com.microsoft.helloworld\",\"appVer\":\"1.0\",\"userId\":\"FE5948CF51113A33D7B3F20E1AD0C88AE457B03B68B8A4D36A159B7203F57C68\",\"tags\":{\"cllVersion\":\"BUILD_LABEL_NOT_SET\",\"cllName\":\"com.microsoft.cll\",\"cV\":\"1ZOrgqtJCJVN89aQ.1\"},\"data\":{\"baseType\":\"Ms.Media.MediaUsage\",\"baseData\":{\"mediaType\":\"Video\",\"lengthMs\":10,\"action\":1,\"positionMs\":0},\"EmailAlias\":\"foobar\",\"HelloWorldMessage\":\"some extra message information\",\"HelloWorldRating\":5,\"HelloWorldFeedback\":\"instrumenting is pretty easy\"}}";
    public static final String singleBadJsonEvent = "{\"ver\"1,\"name\"\"Microsoft.Example.HelloWorldBC\",\"time\":\"2014-12-15T17:44:06.0000231Z\",\"sampleRate\":10.0,\"seq\":\"-5388374316054856560:4\",\"iKey\":\"ASM-AndroidHW\",\"flags\":17,\"deviceId\":\"47D9A290B04EE14EC33DFE1EF84315A8FAE7BB836465C4C814F8ACB5AEDA9F0A\",\"os\":\"Android\",\"osVer\":\"4.4.3\",\"appId\":\"com.microsoft.helloworld\",\"appVer\":\"1.0\",\"userId\":\"FE5948CF51113A33D7B3F20E1AD0C88AE457B03B68B8A4D36A159B7203F57C68\",\"tags\":{\"cllVersion\":\"BUILD_LABEL_NOT_SET\",\"cllName\":\"com.microsoft.cll\",\"cV\":\"1ZOrgqtJCJVN89aQ.1\"},\"data\":{\"baseType\":\"Ms.Media.MediaUsage\",\"baseData\":{\"mediaType\":\"Video\",\"lengthMs\":10,\"action\":1,\"positionMs\":0},\"EmailAlias\":\"foobar\",\"HelloWorldMessage\":\"some extra message information\",\"HelloWorldRating\":5,\"HelloWorldFeedback\":\"instrumenting is pretty easy\"}}";
    public static final String partialEvent = "{\"ver\"1,\"name\"\"Microsoft.Example.HelloWorldBC\"}";
    public static final IJsonSerializable singleGoodABCEvent;
    public static final IJsonSerializable singleGoodCEvent;
    public static final IJsonSerializable singleGoodBCEvent;

    static {
        singleGoodABCEvent = generateABCEvent();
        singleGoodCEvent = generateCEvent();
        singleGoodBCEvent = generateBCEvent();
    }

    public static IJsonSerializable generateBCEvent() {
        final HelloWorldBC helloWorld = new HelloWorldBC();
        helloWorld.setEmailAlias("foobar");
        helloWorld.setHelloWorldFeedback("instrumenting is pretty easy");
        helloWorld.setHelloWorldMessage("some extra message information");
        helloWorld.setHelloWorldRating(5);

        MediaUsage mediaUsage = new MediaUsage();
        mediaUsage.setAction(1);
        mediaUsage.setLengthMs((long)10);
        mediaUsage.setMediaType("Video");
        helloWorld.setBaseData(mediaUsage);

        return helloWorld;
    }

    public static IJsonSerializable generateABCEvent() {
        final HelloWorldBC helloWorld = new HelloWorldBC();
        helloWorld.setEmailAlias("foobar");
        helloWorld.setHelloWorldFeedback("instrumenting is pretty easy");
        helloWorld.setHelloWorldMessage("some extra message information");
        helloWorld.setHelloWorldRating(5);

        MediaUsage mediaUsage = new MediaUsage();
        mediaUsage.setAction(1);
        mediaUsage.setLengthMs((long)10);
        mediaUsage.setMediaType("Video");
        helloWorld.setBaseData(mediaUsage);

        CustomPartA partA = new CustomPartA(new CustomLogger(), "");
        return partA.populate(helloWorld, null);

    }

    public static IJsonSerializable generateCEvent() {
        HelloWorldC helloWorld = new HelloWorldC();
        helloWorld.setEmailAlias("foobar");
        helloWorld.setHelloWorldFeedback("instrumenting is pretty easy");
        helloWorld.setHelloWorldMessage("some extra message information");
        helloWorld.setHelloWorldRating(5);

        CustomPartA partA = new CustomPartA(new CustomLogger(), "");
        return partA.populate(helloWorld, null);
    }
}
