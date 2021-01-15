package com.skeletor.plugin.websockets.incoming.common;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.skeletor.plugin.websockets.clients.WebSocketClient;
import com.skeletor.plugin.websockets.incoming.IncomingWebMessage;
import com.skeletor.plugin.websockets.outgoing.common.YoutubeTVComposer;

public class EditTVEvent extends IncomingWebMessage<EditTVEvent.JSONEditTVEvent> {

    public EditTVEvent() {
        super(JSONEditTVEvent.class);
    }

    @Override
    public void handle(WebSocketClient client, JSONEditTVEvent message) {
        if(!client.getHabbo().getRoomUnit().getRoom().hasRights(client.getHabbo()))
            return;
        HabboItem item = client.getHabbo().getRoomUnit().getRoom().getHabboItem(message.itemId);
        if(item == null)
            return;
        item.setExtradata(message.videoId);
        item.needsUpdate(true);
        client.sendMessage(new YoutubeTVComposer(message.itemId, message.videoId));
    }

    static class JSONEditTVEvent {
        int itemId;
        String videoId;
    }
}
