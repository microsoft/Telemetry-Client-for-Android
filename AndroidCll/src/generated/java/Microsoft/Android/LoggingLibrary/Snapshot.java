/*
 * Generated from Microsoft.Android.LoggingLibrary.bond (https://github.com/Microsoft/bond)
*/
package Microsoft.Android.LoggingLibrary;
import com.microsoft.telemetry.Data;
import com.microsoft.telemetry.IJsonSerializable;

import java.io.IOException;
import java.io.Writer;

/**
 * Data contract class Snapshot.
 */
public class Snapshot extends Data<Ms.Telemetry.CllHeartBeat> implements
    IJsonSerializable
{
    /**
     * Initializes a new instance of the Snapshot class.
     */
    public Snapshot()
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
        this.Attributes.put("Description", "Android's Client Telemetry Snapshot");
    }
    
    /**
     * Optionally initializes fields for the current context.
     */
    protected void InitializeFields() {
        QualifiedName = "Microsoft.Android.LoggingLibrary.Snapshot";
    }
}
