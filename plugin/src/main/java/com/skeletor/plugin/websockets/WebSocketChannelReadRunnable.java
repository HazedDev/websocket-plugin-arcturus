package com.skeletor.plugin.websockets;

import com.eu.habbo.Emulator;
import com.skeletor.plugin.utils.JsonFactory;
import com.skeletor.plugin.websockets.clients.WebSocketClient;
import com.skeletor.plugin.websockets.clients.WebSocketClientManager;
import com.skeletor.plugin.websockets.incoming.IncomingWebMessage;
import com.skeletor.plugin.websockets.incoming.common.SSOTicketEvent;
import io.netty.channel.ChannelHandlerContext;

public class WebSocketChannelReadRunnable implements Runnable {
    private final ChannelHandlerContext ctx;
    private final String msg;

    public WebSocketChannelReadRunnable(ChannelHandlerContext ctx, String msg) {
        this.ctx = ctx;
        this.msg = msg;
    }

    public void run() {
        WebSocketClient client = this.ctx.channel().attr(WebSocketClientManager.CLIENT).get();
        if (client != null) {
            try {
                IncomingWebMessage.JSONIncomingEvent heading = JsonFactory.getInstance().fromJson(msg,  IncomingWebMessage.JSONIncomingEvent.class);
                Class<? extends IncomingWebMessage> message = WebSocketManager.getInstance().getIncomingMessages().get(heading.header);
                IncomingWebMessage webEvent = message.getDeclaredConstructor().newInstance();
                if(client.isAuthenticated() || webEvent.type == SSOTicketEvent.class) {
                    webEvent.handle(client, JsonFactory.getInstance().fromJson(heading.data.toString(), webEvent.type));
                }
            } catch(Exception ex) {
                //Emulator.getLogging().logErrorLine(ex);
            }
        }
    }

}
