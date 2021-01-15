package com.skeletor.plugin.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.skeletor.plugin.websockets.WebSocketManager;
import com.skeletor.plugin.websockets.clients.WebSocketClient;
import com.skeletor.plugin.websockets.outgoing.common.YoutubeTVComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionAlternativeYoutubeTV extends HabboItem {

    public InteractionAlternativeYoutubeTV(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionAlternativeYoutubeTV(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        return false;
    }

    @Override
    public boolean isWalkable() {
        return false;
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {

    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        super.onClick(client, room, objects);
        WebSocketClient wsClient = WebSocketManager.getInstance().getClientManager().getWebSocketClientForHabbo(client.getHabbo().getHabboInfo().getId());
        if(wsClient != null) {
            wsClient.sendMessage(new YoutubeTVComposer(room.hasRights(client.getHabbo()) ? this.getId() : 0, this.getExtradata().equals("0") ? "" : this.getExtradata()));
        }
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage) {
        if (this.getExtradata().length() == 0)
            this.setExtradata("");

        serverMessage.appendInt(1 + (this.isLimited() ? 256 : 0));
        serverMessage.appendInt(2);
        serverMessage.appendString("THUMBNAIL_URL");
        if (this.getExtradata() == null) {
            serverMessage.appendString("");
        } else {
            serverMessage.appendString(Emulator.getConfig().getValue("imager.url.youtube").replace("%video%", this.getExtradata()));
        }
        serverMessage.appendString("videoId");
        serverMessage.appendString(this.getExtradata());

        super.serializeExtradata(serverMessage);

    }
}
