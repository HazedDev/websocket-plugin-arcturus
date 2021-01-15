package com.skeletor.plugin.websockets;

import com.eu.habbo.Emulator;
import com.skeletor.plugin.websockets.clients.WebSocketClient;
import com.skeletor.plugin.websockets.clients.WebSocketClientManager;
import com.skeletor.plugin.websockets.outgoing.common.PingComposer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.io.IOException;

@ChannelHandler.Sharable
public class WebSocketMessageHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ctx.channel().close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        WebSocketManager.getInstance().getClientManager().addClient(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof Exception) {
            if (!(cause instanceof IOException)) {
                //cause.printStackTrace(Logging.getErrorsRuntimeWriter());
            }
        }
        ctx.channel().close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        if(msg instanceof TextWebSocketFrame) {
            try {
                TextWebSocketFrame message = (TextWebSocketFrame) msg;
                WebSocketChannelReadRunnable handler = new WebSocketChannelReadRunnable(ctx, message.text());
                handler.run();
            } catch (Exception e) {
                Emulator.getLogging().logErrorLine(e);
            }
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            WebSocketClient client = ctx.channel().attr(WebSocketClientManager.CLIENT).get();

            if(client == null || !client.isAuthenticated()) {
                ctx.close();
                return;
            }

            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                client.dispose();
            } else if (e.state() == IdleState.WRITER_IDLE) {
                client.sendMessage(new PingComposer(""));
            }
        }

        if (evt instanceof ChannelInputShutdownEvent) {
            ctx.close();
        }
    }
}
