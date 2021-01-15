package com.skeletor.plugin.websockets.outgoing.common;

import com.google.gson.JsonPrimitive;
import com.skeletor.plugin.websockets.outgoing.OutgoingWebMessage;

public class SessionDataComposer extends OutgoingWebMessage {
    public SessionDataComposer(int id, String username, int credits, String look) {
        super("session_data");
        this.data.add("id", new JsonPrimitive(id));
        this.data.add("username", new JsonPrimitive(username));
        this.data.add("credits", new JsonPrimitive(credits));
        this.data.add("look", new JsonPrimitive(look));
    }
}
