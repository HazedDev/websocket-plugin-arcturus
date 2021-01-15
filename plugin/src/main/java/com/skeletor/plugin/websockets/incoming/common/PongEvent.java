package com.skeletor.plugin.websockets.incoming.common;

import com.skeletor.plugin.websockets.clients.WebSocketClient;
import com.skeletor.plugin.websockets.incoming.IncomingWebMessage;

public class PongEvent extends IncomingWebMessage<PongEvent.JSONPongEvent> {

    public PongEvent() {
        super(JSONPongEvent.class);
    }

    @Override
    public void handle(WebSocketClient client, JSONPongEvent message) {
        //client.sendMessage(new PingComposer(message.message));
    }

    static class JSONPongEvent {
        String message;
    }
}
