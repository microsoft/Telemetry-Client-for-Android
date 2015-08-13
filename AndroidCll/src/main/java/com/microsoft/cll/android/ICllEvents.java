package com.microsoft.cll.android;

public interface ICllEvents {
    public void sendComplete();

    public void stopped();

    public void eventDropped(String event);
}
