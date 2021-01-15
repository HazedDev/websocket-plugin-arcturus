package com.skeletor.plugin.websockets.incoming.common;

import com.eu.habbo.Emulator;
import com.skeletor.plugin.websockets.clients.WebSocketClient;
import com.skeletor.plugin.websockets.incoming.IncomingWebMessage;

public class SSOTicketEvent extends IncomingWebMessage<SSOTicketEvent.JSONSSOTicketEvent> {

    public SSOTicketEvent() {
        super(SSOTicketEvent.JSONSSOTicketEvent.class);
    }

    @Override
    public void handle(WebSocketClient client, JSONSSOTicketEvent message) {
        if(!client.tryAuthenticate(message.ticket)) {
            client.dispose();
            return;
        }

        Emulator.getLogging().logUserLine(client.getHabbo().getHabboInfo().getUsername() + " connected to WebSocket Server.");
    }

    static class JSONSSOTicketEvent {
        String ticket;
    }
}
