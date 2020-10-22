package com.convexcreative.propres.event.server;

import com.convexcreative.propres.event.ProPresEvent;

public abstract class ServerStatusChangeEvent extends ProPresEvent {

    public final static int DISCONNECTED = 0;
    public final static int CONNECTING = 1;
    public final static int CONNECTED = 2;

}
