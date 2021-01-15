package com.skeletor.plugin.websockets.outgoing.common;

import com.google.gson.JsonPrimitive;
import com.skeletor.plugin.websockets.outgoing.OutgoingWebMessage;

public class UpdateCreditsComposer extends OutgoingWebMessage {
    public UpdateCreditsComposer(int credits) {
        super("update_credits");
        this.data.add("credits", new JsonPrimitive(credits));
    }
}
