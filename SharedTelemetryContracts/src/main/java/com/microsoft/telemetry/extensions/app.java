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
 * Data contract class app.
 */
public class app extends Extension implements
        IJsonSerializable
{
    /**
     * Backing field for property ExpId.
     */
    private String expId;

    /**
     * Backing field for property UserId.
     */
    private String userId;

    /**
     * Initializes a new instance of the app class.
     */
    public app()
    {
        this.InitializeFields();
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
     * Gets the UserId property.
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * Sets the UserId property.
     */
    public void setUserId(String value) {
        this.userId = value;
    }


    /**
     * Serializes the beginning of this object to the passed in writer.
     * @param writer The writer to serialize this object to.
     */
    protected String serializeContent(Writer writer) throws IOException
    {
        String prefix = super.serializeContent(writer);
        if (!(this.expId == null))
        {
            writer.write(prefix + "\"expId\":");
            writer.write(JsonHelper.convert(this.expId));
            prefix = ",";
        }

        if (!(this.userId == null))
        {
            writer.write(prefix + "\"userId\":");
            writer.write(JsonHelper.convert(this.userId));
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