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
 * Data contract class android.
 */
public class android extends Extension implements
    IJsonSerializable
{
    /**
     * Backing field for property LibVer.
     */
    private String libVer;
    
    /**
     * Initializes a new instance of the android class.
     */
    public android()
    {
        this.InitializeFields();
    }
    
    /**
     * Gets the LibVer property.
     */
    public String getLibVer() {
        return this.libVer;
    }
    
    /**
     * Sets the LibVer property.
     */
    public void setLibVer(String value) {
        this.libVer = value;
    }
    

    /**
     * Serializes the beginning of this object to the passed in writer.
     * @param writer The writer to serialize this object to.
     */
    protected String serializeContent(Writer writer) throws IOException
    {
        String prefix = super.serializeContent(writer);
        if (!(this.libVer == null))
        {
            writer.write(prefix + "\"libVer\":");
            writer.write(JsonHelper.convert(this.libVer));
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
