package io.leego.chat.server;

import io.leego.chat.HandlerName;
import io.leego.chat.handler.ReadIdleHandler;
import io.leego.chat.handler.codec.ByteBufBoxDecoder;
import io.leego.chat.handler.codec.ByteBufBoxEncoder;
import io.leego.chat.server.handle.AuthHandler;
import io.leego.chat.server.handle.AuthTimeoutHandler;
import io.leego.chat.server.handle.ChatServerHandler;
import io.leego.chat.server.handle.LoggerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author Yihleego
 */
public class SocketChatServer extends AbstractChatServer {
    private final MessageToMessageDecoder<?> decoder;
    private final MessageToMessageEncoder<?> encoder;
    private final Duration authTimeout;
    private final Duration idleTimeout;
    private final AuthHandler authHandler;
    private final LoggerHandler loggerHandler;
    private final ChatServerHandler chatServerHandler;

    public SocketChatServer(Integer port, Duration idleTimeout, Duration authTimeout, ChatServerHandler chatServerHandler, AuthHandler authHandler, LoggerHandler loggerHandler) {
        super(port);
        this.decoder = new ByteBufBoxDecoder();
        this.encoder = new ByteBufBoxEncoder();
        this.authTimeout = authTimeout;
        this.idleTimeout = idleTimeout;
        this.authHandler = authHandler;
        this.loggerHandler = loggerHandler;
        this.chatServerHandler = chatServerHandler;
    }

    @Override
    protected final ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) {
                channel.pipeline()
                        .addLast(HandlerName.SOCKET_DECODER, decoder)
                        .addLast(HandlerName.SOCKET_ENCODER, encoder)
                        .addLast(HandlerName.AUTH_TIMEOUT_HANDLER, new AuthTimeoutHandler(authTimeout.toMillis(), TimeUnit.MILLISECONDS))
                        .addLast(HandlerName.AUTH_HANDLER, authHandler)
                        .addLast(HandlerName.READ_IDLE_HANDLER, new ReadIdleHandler(idleTimeout.toMillis(), TimeUnit.MILLISECONDS))
                        .addLast(HandlerName.LOGGER_HANDLER, loggerHandler)
                        .addLast(HandlerName.CHAT_SERVER_HANDLER, chatServerHandler);
            }
        };
    }

}

