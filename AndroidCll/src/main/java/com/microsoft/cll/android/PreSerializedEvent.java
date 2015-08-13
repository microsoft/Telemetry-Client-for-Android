package com.microsoft.cll.android;

import java.util.Map;

public class PreSerializedEvent {
    public Map<String,String> attributes;
    public String partCName;
    public String partBName;
    public String data;

    public PreSerializedEvent(String data, String partCName, String partBName, Map<String, String> attributes) {
        this.data = data;
        this.partBName = partBName;
        this.partCName = partCName;
        this.attributes = attributes;
    }
}
