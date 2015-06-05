/*
 * Generated from Ms.Telemetry.bond (https://github.com/Microsoft/bond)
*/
package Ms.Telemetry;
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
 * Data contract class ClientSnapshot.
 */
public class ClientSnapshot extends Domain implements
    IJsonSerializable
{
    /**
     * Backing field for property LastSnapshotTimeStamp.
     */
    private String lastSnapshotTimeStamp;
    
    /**
     * Backing field for property Uploader.
     */
    private String uploader;
    
    /**
     * Backing field for property UploaderVersion.
     */
    private String uploaderVersion;
    
    /**
     * Backing field for property EventsQueuedForUpload.
     */
    private int eventsQueuedForUpload;
    
    /**
     * Backing field for property RuntimeErrors.
     */
    private int runtimeErrors;
    
    /**
     * Backing field for property EventsDroppedDueToQuota.
     */
    private int eventsDroppedDueToQuota;
    
    /**
     * Backing field for property VortexHttpAttempts.
     */
    private int vortexHttpAttempts;
    
    /**
     * Backing field for property VortexHttpFailures.
     */
    private int vortexHttpFailures;
    
    /**
     * Backing field for property CacheUsagePercent.
     */
    private double cacheUsagePercent;
    
    /**
     * Backing field for property AvgVortexResponseLatencyMs.
     */
    private int avgVortexResponseLatencyMs;
    
    /**
     * Backing field for property MaxVortexResponseLatencyMs.
     */
    private int maxVortexResponseLatencyMs;
    
    /**
     * Backing field for property SettingsHttpAttempts.
     */
    private int settingsHttpAttempts;
    
    /**
     * Backing field for property SettingsHttpFailures.
     */
    private int settingsHttpFailures;
    
    /**
     * Backing field for property AvgSettingsResponseLatencyMs.
     */
    private int avgSettingsResponseLatencyMs;
    
    /**
     * Backing field for property MaxSettingsResponseLatencyMs.
     */
    private int maxSettingsResponseLatencyMs;
    
    /**
     * Initializes a new instance of the ClientSnapshot class.
     */
    public ClientSnapshot()
    {
        this.InitializeFields();
        this.SetupAttributes();
    }
    
    /**
     * Gets the LastSnapshotTimeStamp property.
     */
    public String getLastSnapshotTimeStamp() {
        return this.lastSnapshotTimeStamp;
    }
    
    /**
     * Sets the LastSnapshotTimeStamp property.
     */
    public void setLastSnapshotTimeStamp(String value) {
        this.lastSnapshotTimeStamp = value;
    }
    
    /**
     * Gets the Uploader property.
     */
    public String getUploader() {
        return this.uploader;
    }
    
    /**
     * Sets the Uploader property.
     */
    public void setUploader(String value) {
        this.uploader = value;
    }
    
    /**
     * Gets the UploaderVersion property.
     */
    public String getUploaderVersion() {
        return this.uploaderVersion;
    }
    
    /**
     * Sets the UploaderVersion property.
     */
    public void setUploaderVersion(String value) {
        this.uploaderVersion = value;
    }
    
    /**
     * Gets the EventsQueuedForUpload property.
     */
    public int getEventsQueuedForUpload() {
        return this.eventsQueuedForUpload;
    }
    
    /**
     * Sets the EventsQueuedForUpload property.
     */
    public void setEventsQueuedForUpload(int value) {
        this.eventsQueuedForUpload = value;
    }
    
    /**
     * Gets the RuntimeErrors property.
     */
    public int getRuntimeErrors() {
        return this.runtimeErrors;
    }
    
    /**
     * Sets the RuntimeErrors property.
     */
    public void setRuntimeErrors(int value) {
        this.runtimeErrors = value;
    }
    
    /**
     * Gets the EventsDroppedDueToQuota property.
     */
    public int getEventsDroppedDueToQuota() {
        return this.eventsDroppedDueToQuota;
    }
    
    /**
     * Sets the EventsDroppedDueToQuota property.
     */
    public void setEventsDroppedDueToQuota(int value) {
        this.eventsDroppedDueToQuota = value;
    }
    
    /**
     * Gets the VortexHttpAttempts property.
     */
    public int getVortexHttpAttempts() {
        return this.vortexHttpAttempts;
    }
    
    /**
     * Sets the VortexHttpAttempts property.
     */
    public void setVortexHttpAttempts(int value) {
        this.vortexHttpAttempts = value;
    }
    
    /**
     * Gets the VortexHttpFailures property.
     */
    public int getVortexHttpFailures() {
        return this.vortexHttpFailures;
    }
    
    /**
     * Sets the VortexHttpFailures property.
     */
    public void setVortexHttpFailures(int value) {
        this.vortexHttpFailures = value;
    }
    
    /**
     * Gets the CacheUsagePercent property.
     */
    public double getCacheUsagePercent() {
        return this.cacheUsagePercent;
    }
    
    /**
     * Sets the CacheUsagePercent property.
     */
    public void setCacheUsagePercent(double value) {
        this.cacheUsagePercent = value;
    }
    
    /**
     * Gets the AvgVortexResponseLatencyMs property.
     */
    public int getAvgVortexResponseLatencyMs() {
        return this.avgVortexResponseLatencyMs;
    }
    
    /**
     * Sets the AvgVortexResponseLatencyMs property.
     */
    public void setAvgVortexResponseLatencyMs(int value) {
        this.avgVortexResponseLatencyMs = value;
    }
    
    /**
     * Gets the MaxVortexResponseLatencyMs property.
     */
    public int getMaxVortexResponseLatencyMs() {
        return this.maxVortexResponseLatencyMs;
    }
    
    /**
     * Sets the MaxVortexResponseLatencyMs property.
     */
    public void setMaxVortexResponseLatencyMs(int value) {
        this.maxVortexResponseLatencyMs = value;
    }
    
    /**
     * Gets the SettingsHttpAttempts property.
     */
    public int getSettingsHttpAttempts() {
        return this.settingsHttpAttempts;
    }
    
    /**
     * Sets the SettingsHttpAttempts property.
     */
    public void setSettingsHttpAttempts(int value) {
        this.settingsHttpAttempts = value;
    }
    
    /**
     * Gets the SettingsHttpFailures property.
     */
    public int getSettingsHttpFailures() {
        return this.settingsHttpFailures;
    }
    
    /**
     * Sets the SettingsHttpFailures property.
     */
    public void setSettingsHttpFailures(int value) {
        this.settingsHttpFailures = value;
    }
    
    /**
     * Gets the AvgSettingsResponseLatencyMs property.
     */
    public int getAvgSettingsResponseLatencyMs() {
        return this.avgSettingsResponseLatencyMs;
    }
    
    /**
     * Sets the AvgSettingsResponseLatencyMs property.
     */
    public void setAvgSettingsResponseLatencyMs(int value) {
        this.avgSettingsResponseLatencyMs = value;
    }
    
    /**
     * Gets the MaxSettingsResponseLatencyMs property.
     */
    public int getMaxSettingsResponseLatencyMs() {
        return this.maxSettingsResponseLatencyMs;
    }
    
    /**
     * Sets the MaxSettingsResponseLatencyMs property.
     */
    public void setMaxSettingsResponseLatencyMs(int value) {
        this.maxSettingsResponseLatencyMs = value;
    }
    

    /**
     * Serializes the beginning of this object to the passed in writer.
     * @param writer The writer to serialize this object to.
     */
    protected String serializeContent(Writer writer) throws IOException
    {
        String prefix = super.serializeContent(writer);
        if (!(this.lastSnapshotTimeStamp == null))
        {
            writer.write(prefix + "\"lastSnapshotTimeStamp\":");
            writer.write(JsonHelper.convert(this.lastSnapshotTimeStamp));
            prefix = ",";
        }
        
        writer.write(prefix + "\"uploader\":");
        writer.write(JsonHelper.convert(this.uploader));
        prefix = ",";
        
        if (!(this.uploaderVersion == null))
        {
            writer.write(prefix + "\"uploaderVersion\":");
            writer.write(JsonHelper.convert(this.uploaderVersion));
            prefix = ",";
        }
        
        if (!(this.eventsQueuedForUpload == 0))
        {
            writer.write(prefix + "\"eventsQueuedForUpload\":");
            writer.write(JsonHelper.convert(this.eventsQueuedForUpload));
            prefix = ",";
        }
        
        if (!(this.runtimeErrors == 0))
        {
            writer.write(prefix + "\"runtimeErrors\":");
            writer.write(JsonHelper.convert(this.runtimeErrors));
            prefix = ",";
        }
        
        if (!(this.eventsDroppedDueToQuota == 0))
        {
            writer.write(prefix + "\"eventsDroppedDueToQuota\":");
            writer.write(JsonHelper.convert(this.eventsDroppedDueToQuota));
            prefix = ",";
        }
        
        if (!(this.vortexHttpAttempts == 0))
        {
            writer.write(prefix + "\"vortexHttpAttempts\":");
            writer.write(JsonHelper.convert(this.vortexHttpAttempts));
            prefix = ",";
        }
        
        if (!(this.vortexHttpFailures == 0))
        {
            writer.write(prefix + "\"vortexHttpFailures\":");
            writer.write(JsonHelper.convert(this.vortexHttpFailures));
            prefix = ",";
        }
        
        if (this.cacheUsagePercent > 0.0d)
        {
            writer.write(prefix + "\"cacheUsagePercent\":");
            writer.write(JsonHelper.convert(this.cacheUsagePercent));
            prefix = ",";
        }
        
        if (!(this.avgVortexResponseLatencyMs == 0))
        {
            writer.write(prefix + "\"avgVortexResponseLatencyMs\":");
            writer.write(JsonHelper.convert(this.avgVortexResponseLatencyMs));
            prefix = ",";
        }
        
        if (!(this.maxVortexResponseLatencyMs == 0))
        {
            writer.write(prefix + "\"maxVortexResponseLatencyMs\":");
            writer.write(JsonHelper.convert(this.maxVortexResponseLatencyMs));
            prefix = ",";
        }
        
        if (!(this.settingsHttpAttempts == 0))
        {
            writer.write(prefix + "\"settingsHttpAttempts\":");
            writer.write(JsonHelper.convert(this.settingsHttpAttempts));
            prefix = ",";
        }
        
        if (!(this.settingsHttpFailures == 0))
        {
            writer.write(prefix + "\"settingsHttpFailures\":");
            writer.write(JsonHelper.convert(this.settingsHttpFailures));
            prefix = ",";
        }
        
        if (!(this.avgSettingsResponseLatencyMs == 0))
        {
            writer.write(prefix + "\"avgSettingsResponseLatencyMs\":");
            writer.write(JsonHelper.convert(this.avgSettingsResponseLatencyMs));
            prefix = ",";
        }
        
        if (!(this.maxSettingsResponseLatencyMs == 0))
        {
            writer.write(prefix + "\"maxSettingsResponseLatencyMs\":");
            writer.write(JsonHelper.convert(this.maxSettingsResponseLatencyMs));
            prefix = ",";
        }
        
        return prefix;
    }
    
    /**
     * Sets up the events attributes
     */
    public void SetupAttributes()
    {
        this.Attributes.put("Description", "This event is meant to be sent on a regular basis by all persistent in-process and out-of-process Logging Libraries.");
    }
    
    /**
     * Optionally initializes fields for the current context.
     */
    protected void InitializeFields() {
        QualifiedName = "Ms.Telemetry.ClientSnapshot";
    }
}
