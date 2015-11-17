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
 * Data contract class CllHeartBeat.
 */
public class CllHeartBeat extends Domain implements
    IJsonSerializable
{
    /**
     * Backing field for property LastHeartBeat.
     */
    private String lastHeartBeat;
    
    /**
     * Backing field for property EventsQueued.
     */
    private int eventsQueued;
    
    /**
     * Backing field for property LogFailures.
     */
    private int logFailures;
    
    /**
     * Backing field for property QuotaDropCount.
     */
    private int quotaDropCount;
    
    /**
     * Backing field for property RejectDropCount.
     */
    private int rejectDropCount;
    
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
     * Backing field for property AvgVortexLatencyMs.
     */
    private int avgVortexLatencyMs;
    
    /**
     * Backing field for property MaxVortexLatencyMs.
     */
    private int maxVortexLatencyMs;
    
    /**
     * Backing field for property SettingsHttpAttempts.
     */
    private int settingsHttpAttempts;
    
    /**
     * Backing field for property SettingsHttpFailures.
     */
    private int settingsHttpFailures;
    
    /**
     * Backing field for property AvgSettingsLatencyMs.
     */
    private int avgSettingsLatencyMs;
    
    /**
     * Backing field for property MaxSettingsLatencyMs.
     */
    private int maxSettingsLatencyMs;
    
    /**
     * Backing field for property VortexFailures5xx.
     */
    private int vortexFailures5xx;
    
    /**
     * Backing field for property VortexFailures4xx.
     */
    private int vortexFailures4xx;
    
    /**
     * Backing field for property VortexFailuresTimeout.
     */
    private int vortexFailuresTimeout;
    
    /**
     * Backing field for property SettingsFailures5xx.
     */
    private int settingsFailures5xx;
    
    /**
     * Backing field for property SettingsFailures4xx.
     */
    private int settingsFailures4xx;
    
    /**
     * Backing field for property SettingsFailuresTimeout.
     */
    private int settingsFailuresTimeout;
    
    /**
     * Initializes a new instance of the CllHeartBeat class.
     */
    public CllHeartBeat()
    {
        this.InitializeFields();
        this.SetupAttributes();
    }
    
    /**
     * Gets the LastHeartBeat property.
     */
    public String getLastHeartBeat() {
        return this.lastHeartBeat;
    }
    
    /**
     * Sets the LastHeartBeat property.
     */
    public void setLastHeartBeat(String value) {
        this.lastHeartBeat = value;
    }
    
    /**
     * Gets the EventsQueued property.
     */
    public int getEventsQueued() {
        return this.eventsQueued;
    }
    
    /**
     * Sets the EventsQueued property.
     */
    public void setEventsQueued(int value) {
        this.eventsQueued = value;
    }
    
    /**
     * Gets the LogFailures property.
     */
    public int getLogFailures() {
        return this.logFailures;
    }
    
    /**
     * Sets the LogFailures property.
     */
    public void setLogFailures(int value) {
        this.logFailures = value;
    }
    
    /**
     * Gets the QuotaDropCount property.
     */
    public int getQuotaDropCount() {
        return this.quotaDropCount;
    }
    
    /**
     * Sets the QuotaDropCount property.
     */
    public void setQuotaDropCount(int value) {
        this.quotaDropCount = value;
    }
    
    /**
     * Gets the RejectDropCount property.
     */
    public int getRejectDropCount() {
        return this.rejectDropCount;
    }
    
    /**
     * Sets the RejectDropCount property.
     */
    public void setRejectDropCount(int value) {
        this.rejectDropCount = value;
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
     * Gets the AvgVortexLatencyMs property.
     */
    public int getAvgVortexLatencyMs() {
        return this.avgVortexLatencyMs;
    }
    
    /**
     * Sets the AvgVortexLatencyMs property.
     */
    public void setAvgVortexLatencyMs(int value) {
        this.avgVortexLatencyMs = value;
    }
    
    /**
     * Gets the MaxVortexLatencyMs property.
     */
    public int getMaxVortexLatencyMs() {
        return this.maxVortexLatencyMs;
    }
    
    /**
     * Sets the MaxVortexLatencyMs property.
     */
    public void setMaxVortexLatencyMs(int value) {
        this.maxVortexLatencyMs = value;
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
     * Gets the AvgSettingsLatencyMs property.
     */
    public int getAvgSettingsLatencyMs() {
        return this.avgSettingsLatencyMs;
    }
    
    /**
     * Sets the AvgSettingsLatencyMs property.
     */
    public void setAvgSettingsLatencyMs(int value) {
        this.avgSettingsLatencyMs = value;
    }
    
    /**
     * Gets the MaxSettingsLatencyMs property.
     */
    public int getMaxSettingsLatencyMs() {
        return this.maxSettingsLatencyMs;
    }
    
    /**
     * Sets the MaxSettingsLatencyMs property.
     */
    public void setMaxSettingsLatencyMs(int value) {
        this.maxSettingsLatencyMs = value;
    }
    
    /**
     * Gets the VortexFailures5xx property.
     */
    public int getVortexFailures5xx() {
        return this.vortexFailures5xx;
    }
    
    /**
     * Sets the VortexFailures5xx property.
     */
    public void setVortexFailures5xx(int value) {
        this.vortexFailures5xx = value;
    }
    
    /**
     * Gets the VortexFailures4xx property.
     */
    public int getVortexFailures4xx() {
        return this.vortexFailures4xx;
    }
    
    /**
     * Sets the VortexFailures4xx property.
     */
    public void setVortexFailures4xx(int value) {
        this.vortexFailures4xx = value;
    }
    
    /**
     * Gets the VortexFailuresTimeout property.
     */
    public int getVortexFailuresTimeout() {
        return this.vortexFailuresTimeout;
    }
    
    /**
     * Sets the VortexFailuresTimeout property.
     */
    public void setVortexFailuresTimeout(int value) {
        this.vortexFailuresTimeout = value;
    }
    
    /**
     * Gets the SettingsFailures5xx property.
     */
    public int getSettingsFailures5xx() {
        return this.settingsFailures5xx;
    }
    
    /**
     * Sets the SettingsFailures5xx property.
     */
    public void setSettingsFailures5xx(int value) {
        this.settingsFailures5xx = value;
    }
    
    /**
     * Gets the SettingsFailures4xx property.
     */
    public int getSettingsFailures4xx() {
        return this.settingsFailures4xx;
    }
    
    /**
     * Sets the SettingsFailures4xx property.
     */
    public void setSettingsFailures4xx(int value) {
        this.settingsFailures4xx = value;
    }
    
    /**
     * Gets the SettingsFailuresTimeout property.
     */
    public int getSettingsFailuresTimeout() {
        return this.settingsFailuresTimeout;
    }
    
    /**
     * Sets the SettingsFailuresTimeout property.
     */
    public void setSettingsFailuresTimeout(int value) {
        this.settingsFailuresTimeout = value;
    }
    

    /**
     * Serializes the beginning of this object to the passed in writer.
     * @param writer The writer to serialize this object to.
     */
    protected String serializeContent(Writer writer) throws IOException
    {
        String prefix = super.serializeContent(writer);
        if (!(this.lastHeartBeat == null))
        {
            writer.write(prefix + "\"lastHeartBeat\":");
            writer.write(JsonHelper.convert(this.lastHeartBeat));
            prefix = ",";
        }
        
        if (!(this.eventsQueued == 0))
        {
            writer.write(prefix + "\"eventsQueued\":");
            writer.write(JsonHelper.convert(this.eventsQueued));
            prefix = ",";
        }
        
        if (!(this.logFailures == 0))
        {
            writer.write(prefix + "\"logFailures\":");
            writer.write(JsonHelper.convert(this.logFailures));
            prefix = ",";
        }
        
        if (!(this.quotaDropCount == 0))
        {
            writer.write(prefix + "\"quotaDropCount\":");
            writer.write(JsonHelper.convert(this.quotaDropCount));
            prefix = ",";
        }
        
        if (!(this.rejectDropCount == 0))
        {
            writer.write(prefix + "\"rejectDropCount\":");
            writer.write(JsonHelper.convert(this.rejectDropCount));
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
        
        if (!(this.avgVortexLatencyMs == 0))
        {
            writer.write(prefix + "\"avgVortexLatencyMs\":");
            writer.write(JsonHelper.convert(this.avgVortexLatencyMs));
            prefix = ",";
        }
        
        if (!(this.maxVortexLatencyMs == 0))
        {
            writer.write(prefix + "\"maxVortexLatencyMs\":");
            writer.write(JsonHelper.convert(this.maxVortexLatencyMs));
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
        
        if (!(this.avgSettingsLatencyMs == 0))
        {
            writer.write(prefix + "\"avgSettingsLatencyMs\":");
            writer.write(JsonHelper.convert(this.avgSettingsLatencyMs));
            prefix = ",";
        }
        
        if (!(this.maxSettingsLatencyMs == 0))
        {
            writer.write(prefix + "\"maxSettingsLatencyMs\":");
            writer.write(JsonHelper.convert(this.maxSettingsLatencyMs));
            prefix = ",";
        }
        
        if (!(this.vortexFailures5xx == 0))
        {
            writer.write(prefix + "\"vortexFailures5xx\":");
            writer.write(JsonHelper.convert(this.vortexFailures5xx));
            prefix = ",";
        }
        
        if (!(this.vortexFailures4xx == 0))
        {
            writer.write(prefix + "\"vortexFailures4xx\":");
            writer.write(JsonHelper.convert(this.vortexFailures4xx));
            prefix = ",";
        }
        
        if (!(this.vortexFailuresTimeout == 0))
        {
            writer.write(prefix + "\"vortexFailuresTimeout\":");
            writer.write(JsonHelper.convert(this.vortexFailuresTimeout));
            prefix = ",";
        }
        
        if (!(this.settingsFailures5xx == 0))
        {
            writer.write(prefix + "\"settingsFailures5xx\":");
            writer.write(JsonHelper.convert(this.settingsFailures5xx));
            prefix = ",";
        }
        
        if (!(this.settingsFailures4xx == 0))
        {
            writer.write(prefix + "\"settingsFailures4xx\":");
            writer.write(JsonHelper.convert(this.settingsFailures4xx));
            prefix = ",";
        }
        
        if (!(this.settingsFailuresTimeout == 0))
        {
            writer.write(prefix + "\"settingsFailuresTimeout\":");
            writer.write(JsonHelper.convert(this.settingsFailuresTimeout));
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
        QualifiedName = "Ms.Telemetry.CllHeartBeat";
    }
}
