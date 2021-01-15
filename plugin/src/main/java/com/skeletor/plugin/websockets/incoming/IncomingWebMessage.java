package com.skeletor.plugin.websockets.incoming;

import com.google.gson.JsonObject;
import com.skeletor.plugin.websockets.clients.WebSocketClient;

public abstract class IncomingWebMessage<T> {
    public final Class<T> type;

    public IncomingWebMessage(Class<T> type) {
        this.type = type;
    }

    public abstract void handle(WebSocketClient client, T message);

    public static class JSONIncomingEvent {
        public String header;
        public JsonObject data;
    }
}
