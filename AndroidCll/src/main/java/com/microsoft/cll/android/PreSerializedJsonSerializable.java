package com.microsoft.cll.android;

import com.microsoft.telemetry.Base;
import com.microsoft.telemetry.Data;
import com.microsoft.telemetry.Domain;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class PreSerializedJsonSerializable extends Data {
    public String serializedData;

    public PreSerializedJsonSerializable(String serializedData, String partCName, String partBName, Map<String, String> attributes) {
        this.serializedData = serializedData;
        Data baseData = ((Data)(Base)this);
        baseData.setBaseData(new Domain());
        ((Domain) baseData.getBaseData()).QualifiedName = partBName;
        this.QualifiedName = partCName;
        this.Attributes.putAll(attributes);
    }

    @Override
    public void serialize(Writer writer) throws IOException {
        writer.write(serializedData);
    }
}
