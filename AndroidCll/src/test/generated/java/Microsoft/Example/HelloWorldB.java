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
 * Data contract class HelloWorldB.
 */
public class HelloWorldB extends Data implements
    IJsonSerializable
{
    /**
     * Initializes a new instance of the <see cref="HelloWorldB"/> class.
     */
    public HelloWorldB()
    {
        this.InitializeFields();
        this.SetupAttributes();
    }
    

    /**
     * Serializes the beginning of this object to the passed in writer.
     * @param writer The writer to serialize this object to.
     */
    protected String serializeContent(Writer writer) throws IOException
    {
        String prefix = super.serializeContent(writer);
        return prefix;
    }
    
    /**
     * Sets up the events attributes
     */
    public void SetupAttributes()
    {
        this.Attributes.put("Priority", "Normal");
        this.Attributes.put("Latency", "Normal");
        this.Attributes.put("Description", "Hello World Part B Schema");
    }
    
    /**
     * Optionally initializes fields for the current context.
     */
    protected void InitializeFields() {
        QualifiedName = "Microsoft.Example.HelloWorldB";
    }
}
