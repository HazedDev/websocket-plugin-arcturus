package com.skeletor.plugin.override_packets.incoming;

import com.eu.habbo.messages.incoming.rooms.items.youtube.YoutubeRequestPlaylists;

public class OverrideYoutubeRequestPlaylists extends YoutubeRequestPlaylists {
    @Override
    public void handle() throws Exception {
        // do absolutely nothing
    }
}
