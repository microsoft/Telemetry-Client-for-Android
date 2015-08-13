package Microsoft.Example;
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
 * Data contract class HelloWorldBC.
 */
public class HelloWorldBC extends Data implements
    IJsonSerializable
{
    /**
     * Backing field for property EmailAlias.
     */
    private String emailAlias;
    
    /**
     * Backing field for property HelloWorldMessage.
     */
    private String helloWorldMessage;
    
    /**
     * Backing field for property HelloWorldRating.
     */
    private int helloWorldRating;
    
    /**
     * Backing field for property HelloWorldFeedback.
     */
    private String helloWorldFeedback;
    
    /**
     * Initializes a new instance of the <see cref="HelloWorldBC"/> class.
     */
    public HelloWorldBC()
    {
        this.InitializeFields();
        this.SetupAttributes();
    }
    
    /**
     * Gets the EmailAlias property.
     */
    public String getEmailAlias() {
        return this.emailAlias;
    }
    
    /**
     * Sets the EmailAlias property.
     */
    public void setEmailAlias(String value) {
        this.emailAlias = value;
    }
    
    /**
     * Gets the HelloWorldMessage property.
     */
    public String getHelloWorldMessage() {
        return this.helloWorldMessage;
    }
    
    /**
     * Sets the HelloWorldMessage property.
     */
    public void setHelloWorldMessage(String value) {
        this.helloWorldMessage = value;
    }
    
    /**
     * Gets the HelloWorldRating property.
     */
    public int getHelloWorldRating() {
        return this.helloWorldRating;
    }
    
    /**
     * Sets the HelloWorldRating property.
     */
    public void setHelloWorldRating(int value) {
        this.helloWorldRating = value;
    }
    
    /**
     * Gets the HelloWorldFeedback property.
     */
    public String getHelloWorldFeedback() {
        return this.helloWorldFeedback;
    }
    
    /**
     * Sets the HelloWorldFeedback property.
     */
    public void setHelloWorldFeedback(String value) {
        this.helloWorldFeedback = value;
    }
    

    /**
     * Serializes the beginning of this object to the passed in writer.
     * @param writer The writer to serialize this object to.
     */
    protected String serializeContent(Writer writer) throws IOException
    {
        String prefix = super.serializeContent(writer);
        if (!(this.emailAlias == null))
        {
            writer.write(prefix + "\"EmailAlias\":");
            writer.write(JsonHelper.convert(this.emailAlias));
            prefix = ",";
        }
        
        if (!(this.helloWorldMessage == null))
        {
            writer.write(prefix + "\"HelloWorldMessage\":");
            writer.write(JsonHelper.convert(this.helloWorldMessage));
            prefix = ",";
        }
        
        if (!(this.helloWorldRating == 0))
        {
            writer.write(prefix + "\"HelloWorldRating\":");
            writer.write(JsonHelper.convert(this.helloWorldRating));
            prefix = ",";
        }
        
        if (!(this.helloWorldFeedback == null))
        {
            writer.write(prefix + "\"HelloWorldFeedback\":");
            writer.write(JsonHelper.convert(this.helloWorldFeedback));
            prefix = ",";
        }
        
        return prefix;
    }
    
    /**
     * Sets up the events attributes
     */
    public void SetupAttributes()
    {
        this.Attributes.put("Priority", "Normal");
        this.Attributes.put("Latency", "Normal");
        this.Attributes.put("Description", "Hello World Part B and C Schema");
    }
    
    /**
     * Optionally initializes fields for the current context.
     */
    protected void InitializeFields() {
        QualifiedName = "Microsoft.Example.HelloWorldBC";
    }
}
