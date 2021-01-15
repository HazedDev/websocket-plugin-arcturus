package com.skeletor.plugin.websockets.outgoing.common;

import com.google.gson.JsonPrimitive;
import com.skeletor.plugin.websockets.outgoing.OutgoingWebMessage;

public class YoutubeTVComposer extends OutgoingWebMessage {

    public YoutubeTVComposer(int itemId, String videoId) {
        super("youtube_tv");
        this.data.add("itemId", new JsonPrimitive(itemId));
        this.data.add("videoId", new JsonPrimitive(videoId));
    }
}
