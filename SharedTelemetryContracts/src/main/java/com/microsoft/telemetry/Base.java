/*
 * Generated from Microsoft.Telemetry.bond (https://github.com/Microsoft/bond)
*/
package com.microsoft.telemetry;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import com.microsoft.telemetry.ITelemetry;
import com.microsoft.telemetry.ITelemetryData;
import com.microsoft.telemetry.IContext;
import com.microsoft.telemetry.IJsonSerializable;
import com.microsoft.telemetry.Base;
import com.microsoft.telemetry.Data;
import com.microsoft.telemetry.Domain;
import com.microsoft.telemetry.Extension;
import com.microsoft.telemetry.JsonHelper;

/**
 * Data contract class Base.
 */
public class Base implements
    IJsonSerializable
{
    /**
     * A map for holding event attributes.
     */
    public LinkedHashMap<String, String> Attributes = new LinkedHashMap<String, String>();
    
    /**
     * The name for this type
     */
    public String QualifiedName;
    
    /**
     * Backing field for property BaseType.
     */
    private String baseType;
    
    /**
     * Initializes a new instance of the Base class.
     */
    public Base()
    {
        this.InitializeFields();
    }
    
    /**
     * Gets the BaseType property.
     */
    public String getBaseType() {
        return this.baseType;
    }
    
    /**
     * Sets the BaseType property.
     */
    public void setBaseType(String value) {
        this.baseType = value;
    }
    

    /**
     * Serializes the beginning of this object to the passed in writer.
     * @param writer The writer to serialize this object to.
     */
    @Override
    public void serialize(Writer writer) throws IOException
    {
        if (writer == null)
        {
            throw new IllegalArgumentException("writer");
        }
        
        writer.write('{');
        this.serializeContent(writer);
        writer.write('}');
    }

    /**
     * Serializes the beginning of this object to the passed in writer.
     * @param writer The writer to serialize this object to.
     */
    protected String serializeContent(Writer writer) throws IOException
    {
        String prefix = "";
        if (!(this.baseType == null))
        {
            writer.write(prefix + "\"baseType\":");
            writer.write(JsonHelper.convert(this.baseType));
            prefix = ",";
        }
        
        return prefix;
    }
    
    /**
     * Optionally initializes fields for the current context.
     */
    protected void InitializeFields() {
        
    }
}
