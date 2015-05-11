package Ms.Media;
import java.io.IOException;
import java.io.Writer;
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
 * Data contract class MediaUsage.
 */
public class MediaUsage extends Domain implements
    IJsonSerializable
{
    /**
     * Backing field for property MediaType.
     */
    private String mediaType;
    
    /**
     * Backing field for property LengthMs.
     */
    private Long lengthMs;
    
    /**
     * Backing field for property Action.
     */
    private int action;
    
    /**
     * Backing field for property PositionMs.
     */
    private Long positionMs;
    
    /**
     * Initializes a new instance of the <see cref="MediaUsage"/> class.
     */
    public MediaUsage()
    {
        this.InitializeFields();
        this.SetupAttributes();
    }
    
    /**
     * Gets the MediaType property.
     */
    public String getMediaType() {
        return this.mediaType;
    }
    
    /**
     * Sets the MediaType property.
     */
    public void setMediaType(String value) {
        this.mediaType = value;
    }
    
    /**
     * Gets the LengthMs property.
     */
    public Long getLengthMs() {
        return this.lengthMs;
    }
    
    /**
     * Sets the LengthMs property.
     */
    public void setLengthMs(Long value) {
        this.lengthMs = value;
    }
    
    /**
     * Gets the Action property.
     */
    public int getAction() {
        return this.action;
    }
    
    /**
     * Sets the Action property.
     */
    public void setAction(int value) {
        this.action = value;
    }
    
    /**
     * Gets the PositionMs property.
     */
    public Long getPositionMs() {
        return this.positionMs;
    }
    
    /**
     * Sets the PositionMs property.
     */
    public void setPositionMs(Long value) {
        this.positionMs = value;
    }
    

    /**
     * Serializes the beginning of this object to the passed in writer.
     * @param writer The writer to serialize this object to.
     */
    protected String serializeContent(Writer writer) throws IOException
    {
        String prefix = super.serializeContent(writer);
        if (!(this.mediaType == null))
        {
            writer.write(prefix + "\"mediaType\":");
            writer.write(JsonHelper.convert(this.mediaType));
            prefix = ",";
        }
        
        if (!(this.lengthMs == null))
        {
            writer.write(prefix + "\"lengthMs\":");
            writer.write(JsonHelper.convert(this.lengthMs));
            prefix = ",";
        }
        
        if (!(this.action == 0))
        {
            writer.write(prefix + "\"action\":");
            writer.write(JsonHelper.convert(this.action));
            prefix = ",";
        }
        
        if (!(this.positionMs == null))
        {
            writer.write(prefix + "\"positionMs\":");
            writer.write(JsonHelper.convert(this.positionMs));
            prefix = ",";
        }
        
        return prefix;
    }
    
    /**
     * Sets up the events attributes
     */
    public void SetupAttributes()
    {
        this.Attributes.put("Description", "Media Usage Part B Schema");
    }
    
    /**
     * Optionally initializes fields for the current context.
     */
    protected void InitializeFields() {
        QualifiedName = "Ms.Media.MediaUsage";
    }
}
