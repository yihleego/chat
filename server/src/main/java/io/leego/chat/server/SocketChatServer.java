package io.leego.chat.server;

import io.leego.chat.constant.HandlerName;
import io.leego.chat.server.handle.AuthHandler;
import io.leego.chat.server.handle.AuthTimeoutHandler;
import io.leego.chat.server.handle.ChatServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author Yihleego
 */
public class SocketChatServer extends AbstractChatServer {
    private final AuthHandler authHandler;
    private final ChatServerHandler chatServerHandler;
    private final Duration idleTimeout;
    private final Duration authTimeout;

    public SocketChatServer(Integer port, Duration idleTimeout, Duration authTimeout, AuthHandler authHandler, ChatServerHandler chatServerHandler) {
        super(port);
        this.authHandler = authHandler;
        this.chatServerHandler = chatServerHandler;
        this.idleTimeout = idleTimeout;
        this.authTimeout = authTimeout;
    }

    @Override
    protected final ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) {
                channel.pipeline()
                        .addLast(HandlerName.AUTH_TIMEOUT_HANDLER, new AuthTimeoutHandler(authTimeout.toMillis(), TimeUnit.MILLISECONDS))
                        .addLast(HandlerName.AUTH_HANDLER, authHandler)
                        .addLast(HandlerName.IDLE_TIMEOUT_HANDLER, new IdleStateHandler(idleTimeout.toMillis(), 0L, 0L, TimeUnit.MILLISECONDS))
                        .addLast(HandlerName.CHAT_SERVER_HANDLER, chatServerHandler);
            }
        };
    }

}

