/*
 * Generated from Microsoft.Telemetry.Extensions.bond (https://github.com/Microsoft/bond)
*/
package com.microsoft.telemetry.extensions;
import com.microsoft.telemetry.Extension;
import com.microsoft.telemetry.IJsonSerializable;
import com.microsoft.telemetry.JsonHelper;

import java.io.IOException;
import java.io.Writer;

/**
 * Data contract class device.
 */
public class device extends Extension implements
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
     * Backing field for property AuthSecId.
     */
    private String authSecId;
    
    /**
     * Backing field for property DeviceClass.
     */
    private String deviceClass;
    
    /**
     * Initializes a new instance of the device class.
     */
    public device()
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
     * Gets the AuthSecId property.
     */
    public String getAuthSecId() {
        return this.authSecId;
    }
    
    /**
     * Sets the AuthSecId property.
     */
    public void setAuthSecId(String value) {
        this.authSecId = value;
    }
    
    /**
     * Gets the DeviceClass property.
     */
    public String getDeviceClass() {
        return this.deviceClass;
    }
    
    /**
     * Sets the DeviceClass property.
     */
    public void setDeviceClass(String value) {
        this.deviceClass = value;
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
        
        if (!(this.authSecId == null))
        {
            writer.write(prefix + "\"authSecId\":");
            writer.write(JsonHelper.convert(this.authSecId));
            prefix = ",";
        }
        
        if (!(this.deviceClass == null))
        {
            writer.write(prefix + "\"deviceClass\":");
            writer.write(JsonHelper.convert(this.deviceClass));
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
