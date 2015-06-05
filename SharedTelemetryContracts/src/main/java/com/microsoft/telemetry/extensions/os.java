/*
 * Generated from Microsoft.Telemetry.Extensions.bond (https://github.com/Microsoft/bond)
*/
package com.microsoft.telemetry.extensions;
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
 * Data contract class os.
 */
public class os extends Extension implements
    IJsonSerializable
{
    /**
     * Backing field for property Locale.
     */
    private String locale;
    
    /**
     * Backing field for property ExpId.
     */
    private String expId;
    
    /**
     * Initializes a new instance of the os class.
     */
    public os()
    {
        this.InitializeFields();
    }
    
    /**
     * Gets the Locale property.
     */
    public String getLocale() {
        return this.locale;
    }
    
    /**
     * Sets the Locale property.
     */
    public void setLocale(String value) {
        this.locale = value;
    }
    
    /**
     * Gets the ExpId property.
     */
    public String getExpId() {
        return this.expId;
    }
    
    /**
     * Sets the ExpId property.
     */
    public void setExpId(String value) {
        this.expId = value;
    }
    

    /**
     * Serializes the beginning of this object to the passed in writer.
     * @param writer The writer to serialize this object to.
     */
    protected String serializeContent(Writer writer) throws IOException
    {
        String prefix = super.serializeContent(writer);
        if (!(this.locale == null))
        {
            writer.write(prefix + "\"locale\":");
            writer.write(JsonHelper.convert(this.locale));
            prefix = ",";
        }
        
        if (!(this.expId == null))
        {
            writer.write(prefix + "\"expId\":");
            writer.write(JsonHelper.convert(this.expId));
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
