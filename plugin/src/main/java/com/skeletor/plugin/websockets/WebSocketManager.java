package com.skeletor.plugin.websockets;

import com.eu.habbo.Emulator;
import com.eu.habbo.networking.Server;
import com.skeletor.plugin.websockets.clients.WebSocketClientManager;
import com.skeletor.plugin.websockets.incoming.IncomingWebMessage;
import com.skeletor.plugin.websockets.incoming.common.EditTVEvent;
import com.skeletor.plugin.websockets.incoming.common.MoveAvatarEvent;
import com.skeletor.plugin.websockets.incoming.common.PongEvent;
import com.skeletor.plugin.websockets.incoming.common.SSOTicketEvent;
import com.skeletor.plugin.websockets.security.SSLCertificateLoader;
import gnu.trove.map.hash.THashMap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;

public class WebSocketManager extends Server {
    public static WebSocketManager instance;

    private final WebSocketClientManager clientManager;
    private final THashMap<String, Class<? extends IncomingWebMessage>> incomingMessages;
    private final SslContext context;
    private final boolean SSL;

    public WebSocketManager() throws Exception {
        super("WebSocket Server", Emulator.getConfig().getValue("ws.host", "127.0.0.1"), Emulator.getConfig().getInt("ws.port", 2053), 1, 2);
        this.clientManager = new WebSocketClientManager();
        this.incomingMessages = new THashMap<>();
        context = SSLCertificateLoader.getContext(Emulator.getConfig().getValue("ws.cert.pass", ""));
        SSL = context != null;
        initializeMessages();
    }

    public static void Init() {
        try {
            instance = new WebSocketManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initializePipeline() {
        super.initializePipeline();
        this.serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                if(SSL) {
                    ch.pipeline().addLast(context.newHandler(ch.alloc()));
                }
                ch.pipeline().addLast(new HttpServerCodec());
                ch.pipeline().addLast(new HttpObjectAggregator(65536));
                ch.pipeline().addLast(new WebSocketServerProtocolHandler("/", true));
                ch.pipeline().addLast(new IdleStateHandler(60, 30, 0));
                ch.pipeline().addLast(new WebSocketMessageHandler());
            }
        });
    }

    public void initializeMessages() {
        this.registerMessage("sso", SSOTicketEvent.class);
        this.registerMessage("pong", PongEvent.class);
        this.registerMessage("edit_tv", EditTVEvent.class);
        this.registerMessage("move_avatar", MoveAvatarEvent.class);
    }

    public void registerMessage(String key, Class<? extends IncomingWebMessage> message) {
        this.incomingMessages.put(key, message);
    }

    public THashMap<String, Class<? extends IncomingWebMessage>> getIncomingMessages() {
        return this.incomingMessages;
    }

    public static WebSocketManager getInstance() {
        if (instance == null) {
            try {
                instance = new WebSocketManager();
            } catch (Exception e) {
                Emulator.getLogging().logErrorLine(e.getMessage());
            }
        }
        return instance;
    }

    public WebSocketClientManager getClientManager() {
        return this.clientManager;
    }

    public boolean isSSL() {
        return SSL;
    }

    public void Dispose() {
        clientManager.dispose();
        incomingMessages.clear();
        instance = null;
    }

}
