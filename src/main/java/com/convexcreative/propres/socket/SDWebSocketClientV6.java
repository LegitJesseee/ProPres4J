package com.convexcreative.propres.socket;

import com.convexcreative.propres.ProPresAPI;
import com.convexcreative.propres.event.server.*;
import com.convexcreative.propres.event.slide.SlideChangeEvent;
import com.convexcreative.propres.serializable.Slide;
import com.convexcreative.propres.serializable.StageDisplayData;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class SDWebSocketClientV6 extends WebSocketClient {

    private String AUTH;
    private ProPresAPI api;

    public SDWebSocketClientV6(URI serverUri, ProPresAPI api, String password) {
        super(serverUri);
        ProPresAPI.log("Loading API V6...");
        this.api = api;
        AUTH = "{\"pwd\":\"" + password + "\",\"ptl\":610,\"acn\":\"ath\"}";
    }

    public void onOpen(ServerHandshake serverHandshake) {
        ProPresAPI.log("Successfully connected to server.");
        ProPresAPI.getInstance().triggerEvent(ServerConnectEvent.class, "Connected to server! (" + serverHandshake.getHttpStatusMessage() + ")");
        ProPresAPI.getInstance().triggerEvent(ServerStatusChangeEvent.class, ServerStatusChangeEvent.CONNECTED);

        this.send(AUTH);
    }

    public void onMessage(String s) {

        ProPresAPI.getInstance().triggerEvent(ServerMessageReceived.class, s);

        if(s.startsWith("{\"acn\":\"ath\",\"ath\":false")){
            ProPresAPI.getInstance().triggerEvent(ServerAuthFailed.class);
        }

        if(s.contains("\"acn\":\"fv\"")){

            final Slide[] slides = new Slide[] {
                    ProPresAPI.GSON.fromJson(s, StageDisplayData.class).getCurrentSlide(),
                    ProPresAPI.GSON.fromJson(s, StageDisplayData.class).getNextSlide()
            };


            ProPresAPI.getInstance().triggerEvent(SlideChangeEvent.class, slides);

        }else{
            // unsupported message received.
        }

    }

    public void onClose(int code, String reason, boolean remote) {
        final String msg = "Connection closed by " + ( remote ? "remote peer. |" : "us. |" ) + " Code: " + code + (reason==null||reason=="" ?  "" : "| Reason: " + reason);
        ProPresAPI.getInstance().triggerEvent(ServerDisconnectEvent.class, msg, code);

        ProPresAPI.getInstance().triggerEvent(ServerStatusChangeEvent.class, ServerStatusChangeEvent.DISCONNECTED);


        ProPresAPI.log("Disconnected from the server.");
        ProPresAPI.log(msg);
    }

    public void onError(Exception e) {
        ProPresAPI.getInstance().triggerEvent(ServerConnectionErrorEvent.class, e.getMessage());
        ProPresAPI.getInstance().triggerEvent(ServerStatusChangeEvent.class, ServerStatusChangeEvent.DISCONNECTED);

        ProPresAPI.log("Connection error!");
        e.printStackTrace();
    }
}
