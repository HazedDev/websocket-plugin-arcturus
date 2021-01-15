package com.skeletor.plugin.websockets.outgoing.common;

import com.google.gson.JsonPrimitive;
import com.skeletor.plugin.websockets.outgoing.OutgoingWebMessage;

public class PingComposer extends OutgoingWebMessage {

    public PingComposer(String message) {
        super("ping");
        this.data.add("message", new JsonPrimitive(message));
    }
}
