package com.skeletor.plugin.websockets.clients;

import com.eu.habbo.Emulator;
import com.skeletor.plugin.websockets.outgoing.OutgoingWebMessage;
import io.netty.channel.*;
import io.netty.util.AttributeKey;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WebSocketClientManager {
    public static final AttributeKey<WebSocketClient> CLIENT = AttributeKey.valueOf("WebSocketClient");
    private final ConcurrentMap<ChannelId, WebSocketClient> clients;

    public WebSocketClientManager() {
        this.clients = new ConcurrentHashMap<>();
    }

    public boolean addClient(ChannelHandlerContext ctx) {
        WebSocketClient client = new WebSocketClient(ctx.channel());
        ctx.channel().closeFuture().addListener( (ChannelFutureListener) channelFuture ->
                this.disposeClient(ctx.channel())
        );

        ctx.channel().attr(CLIENT).set(client);
        ctx.fireChannelRegistered();

        return this.clients.putIfAbsent(ctx.channel().id(), client) == null;
    }

    public void broadcastMessage(OutgoingWebMessage message) {
        for (WebSocketClient client : this.clients.values()) {
            client.sendMessage(message);
        }
    }

    public void broadcastMessage(OutgoingWebMessage message, WebSocketClient exclude) {
        for (WebSocketClient client : this.clients.values()) {
            if (client.equals(exclude))
                continue;

            client.sendMessage(message);
        }
    }

    public void broadcastMessage(OutgoingWebMessage message, String minPermission, WebSocketClient exclude) {
        for (WebSocketClient client : this.clients.values()) {
            if (client.equals(exclude))
                continue;

            if (client.getHabbo() != null) {
                if (client.getHabbo().hasPermission(minPermission)) {
                    client.sendMessage(message);
                }
            }
        }
    }

    public ConcurrentMap<ChannelId, WebSocketClient> getClients() {
        return clients;
    }

    public WebSocketClient getWebSocketClientForHabbo(int id) {
        for(WebSocketClient client : this.clients.values()) {
            if(client.getHabbo() == null)
                continue;
            if(client.getHabbo().getHabboInfo().getId() == id)
                return client;
        }
        return null;
    }

    public boolean containsHabbo(Integer id) {
        if (!this.clients.isEmpty()) {
            for (WebSocketClient client : this.clients.values()) {
                if (client.getHabbo() != null) {
                    if (client.getHabbo().getHabboInfo() != null) {
                        if (client.getHabbo().getHabboInfo().getId() == id)
                            return true;
                    }
                }
            }
        }
        return false;
    }

    public void disposeClient(WebSocketClient client) {
        this.disposeClient(client.getChannel());
    }

    private void disposeClient(Channel channel) {
        WebSocketClient client = channel.attr(CLIENT).get();

        if (client != null) {
            if(client.getHabbo() != null)
                Emulator.getLogging().logUserLine(client.getHabbo().getHabboInfo().getUsername() + " disconnected from WebSocket Server.");
            client.dispose();
        }
        channel.deregister();
        channel.attr(CLIENT).set(null);
        channel.closeFuture();
        channel.close();
        this.clients.remove(channel.id());
    }

    public void dispose() {
        clients.forEach( (k,v) -> {
            v.dispose();
            v.getChannel().deregister();
            v.getChannel().attr(CLIENT).set(null);
            v.getChannel().closeFuture();
            v.getChannel().close();
        });
        clients.clear();
    }
}
