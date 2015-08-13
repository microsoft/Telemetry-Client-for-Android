package com.microsoft.cll.android;

/**
 * This class handles any custom events that occur during cll execution
 */
public class CllEvents  implements ICllEvents
{

    private final PartA partA;
    private final ClientTelemetry clientTelemetry;
    private final Cll cll;

    public CllEvents(PartA partA, ClientTelemetry clientTelemetry, Cll cll) {
        this.partA              = partA;
        this.clientTelemetry    = clientTelemetry;
        this.cll                = cll;
    }

    @Override
    public void sendComplete()
    {
    }

    @Override
    public void stopped()
    {
    }

    @Override
    public void eventDropped(String event)
    {
    }
}
