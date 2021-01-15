package com.skeletor.plugin;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.ItemInteraction;
import com.eu.habbo.habbohotel.items.ItemManager;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.PacketManager;
import com.eu.habbo.messages.incoming.Incoming;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.HabboPlugin;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadItemsManagerEvent;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadedEvent;
import com.eu.habbo.plugin.events.users.UserCreditsEvent;
import com.eu.habbo.plugin.events.users.UserDisconnectEvent;
import com.eu.habbo.plugin.events.users.UserLoginEvent;
import com.skeletor.plugin.interactions.InteractionAlternativeYoutubeTV;
import com.skeletor.plugin.override_packets.incoming.OverrideYoutubeRequestPlaylists;
import com.skeletor.plugin.websockets.WebSocketManager;
import com.skeletor.plugin.websockets.clients.WebSocketClient;
import com.skeletor.plugin.websockets.outgoing.common.SessionDataComposer;
import com.skeletor.plugin.websockets.outgoing.common.UpdateCreditsComposer;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.lang.reflect.Field;


public class main extends HabboPlugin implements EventListener {
    public void onEnable () throws Exception {
        Emulator.getPluginManager().registerEvents(this, this);
        if(Emulator.isReady && !Emulator.isShuttingDown) {
            this.onEmulatorLoadedEvent(null);
        }

    }

    @EventHandler
    public void onEmulatorLoadedEvent ( EmulatorLoadedEvent e ) throws Exception {
        //add missing db entries
        Emulator.getConfig().register("ws.host", "127.0.0.1");
        Emulator.getConfig().register("ws.port", "90");
        Emulator.getConfig().register("ws.cert.pass", "CHANGEIT");

        WebSocketManager.Init();
        WebSocketManager.getInstance().initializePipeline();
        WebSocketManager.getInstance().connect();

        Emulator.getLogging ().logStart ( "WebSocket Server on: " +
                (WebSocketManager.getInstance().isSSL() ? "wss" : "ws") + "://" +
                WebSocketManager.getInstance().getHost() + ":" + WebSocketManager.getInstance().getPort());

        //replacing youtube tv packet
        PacketManager packetManager = Emulator.getGameServer().getPacketManager();
        Field f = PacketManager.class.getDeclaredField("incoming");
        f.setAccessible(true);
        THashMap<Integer, Class<? extends MessageHandler>> incoming = (THashMap<Integer, Class<? extends MessageHandler>>)f.get(packetManager);
        incoming.remove(Incoming.YoutubeRequestPlaylists);
        Emulator.getGameServer().getPacketManager().registerHandler(Incoming.YoutubeRequestPlaylists, OverrideYoutubeRequestPlaylists.class);
    }

    @EventHandler
    public void onUserDisconnectEvent(UserDisconnectEvent e) {
        WebSocketClient conn = WebSocketManager.getInstance().getClientManager().getWebSocketClientForHabbo(e.habbo.getHabboInfo().getId());
        if(conn != null) {
            WebSocketManager.getInstance().getClientManager().disposeClient(conn);
        }
    }

    @EventHandler
    public void onUserLoginEvent(UserLoginEvent e) {
        WebSocketClient conn = WebSocketManager.getInstance().getClientManager().getWebSocketClientForHabbo(e.habbo.getHabboInfo().getId());
        if(conn != null) {
            conn.sendMessage(new SessionDataComposer(e.habbo.getHabboInfo().getId(), e.habbo.getHabboInfo().getUsername(), e.habbo.getHabboInfo().getCredits(), e.habbo.getHabboInfo().getLook()));
        }
    }

    @EventHandler
    public void onUserCreditsEvent(UserCreditsEvent e) {
        WebSocketClient conn = WebSocketManager.getInstance().getClientManager().getWebSocketClientForHabbo(e.habbo.getHabboInfo().getId());
        if(conn != null) {
            conn.sendMessage(new UpdateCreditsComposer(e.credits));
        }
    }

    @EventHandler
    public void onLoadItemsManager(EmulatorLoadItemsManagerEvent e) throws NoSuchFieldException, IllegalAccessException {
        ItemManager manager = Emulator.getGameEnvironment().getItemManager();
        Field f = ItemManager.class.getDeclaredField("interactionsList");
        f.setAccessible(true);
        THashSet<ItemInteraction> itemInteractions = (THashSet<ItemInteraction>) f.get(manager);
        itemInteractions.removeIf(itemInteraction -> itemInteraction.getName().equals("youtube"));
        Emulator.getGameEnvironment().getItemManager().addItemInteraction(new ItemInteraction("youtube", InteractionAlternativeYoutubeTV.class));
    }

    @Override
    public void onDisable() throws Exception {
        WebSocketManager.getInstance().stop();
        WebSocketManager.getInstance().Dispose();
    }

    public boolean hasPermission(Habbo habbo, String s) {
        return false;
    }
}
