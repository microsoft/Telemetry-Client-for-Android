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
 * Data contract class user.
 */
public class user extends Extension implements
    IJsonSerializable
{
    /**
     * Backing field for property Id.
     */
    private String id;
    
    /**
     * Backing field for property LocalId.
     */
    private String localId;
    
    /**
     * Backing field for property AuthId.
     */
    private String authId;
    
    /**
     * Initializes a new instance of the user class.
     */
    public user()
    {
        this.InitializeFields();
    }
    
    /**
     * Gets the Id property.
     */
    public String getId() {
        return this.id;
    }
    
    /**
     * Sets the Id property.
     */
    public void setId(String value) {
        this.id = value;
    }
    
    /**
     * Gets the LocalId property.
     */
    public String getLocalId() {
        return this.localId;
    }
    
    /**
     * Sets the LocalId property.
     */
    public void setLocalId(String value) {
        this.localId = value;
    }
    
    /**
     * Gets the AuthId property.
     */
    public String getAuthId() {
        return this.authId;
    }
    
    /**
     * Sets the AuthId property.
     */
    public void setAuthId(String value) {
        this.authId = value;
    }
    

    /**
     * Serializes the beginning of this object to the passed in writer.
     * @param writer The writer to serialize this object to.
     */
    protected String serializeContent(Writer writer) throws IOException
    {
        String prefix = super.serializeContent(writer);
        if (!(this.id == null))
        {
            writer.write(prefix + "\"id\":");
            writer.write(JsonHelper.convert(this.id));
            prefix = ",";
        }
        
        if (!(this.localId == null))
        {
            writer.write(prefix + "\"localId\":");
            writer.write(JsonHelper.convert(this.localId));
            prefix = ",";
        }
        
        if (!(this.authId == null))
        {
            writer.write(prefix + "\"authId\":");
            writer.write(JsonHelper.convert(this.authId));
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
