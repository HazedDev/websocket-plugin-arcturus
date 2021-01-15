package com.skeletor.plugin.utils;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;

import java.util.Iterator;

public class UtilityMethods {
    /**
     * Returns the <b>online</b> Habbo user associated with a certain sso ticket.
     * Returns null if no such user is found.
     * @param sso
     * @return Habbo
     */
    public static Habbo getUserWithSSO(String sso) {
        Iterator iterator = Emulator.getGameServer().getGameClientManager().getSessions().values().iterator();

        GameClient client;
        do {
            if (!iterator.hasNext()) {
                return null;
            }

            client = (GameClient)iterator.next();
        } while(client.getHabbo() == null || !client.getHabbo().getHabboInfo().getSso().equals(sso));

        return client.getHabbo();
    }
}
