package io.leego.chat.server;

import io.leego.chat.constant.HandlerName;
import io.leego.chat.server.handle.AuthHandler;
import io.leego.chat.server.handle.ChatServerHandler;
import io.leego.chat.server.handle.IdleTimeoutHandler;
import io.leego.chat.server.handle.LoggerHandler;
import io.leego.chat.server.handle.codec.WebSocketFrameBoxDecoder;
import io.leego.chat.server.handle.codec.WebSocketFrameBoxEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author Yihleego
 */
public class WebSocketChatServer extends AbstractChatServer {
    private final String path;
    private final MessageToMessageDecoder<?> decoder;
    private final MessageToMessageEncoder<?> encoder;
    private final Duration authTimeout;
    private final Duration idleTimeout;
    private final AuthHandler authHandler;
    private final LoggerHandler loggerHandler;
    private final ChatServerHandler chatServerHandler;

    public WebSocketChatServer(Integer port, String path, Duration idleTimeout, Duration authTimeout, ChatServerHandler chatServerHandler, AuthHandler authHandler, LoggerHandler loggerHandler) {
        super(port);
        this.path = path != null ? path : "";
        this.decoder = new WebSocketFrameBoxDecoder();
        this.encoder = new WebSocketFrameBoxEncoder();
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
                        .addLast(HandlerName.HTTP_CODEC, new HttpServerCodec())
                        .addLast(HandlerName.HTTP_CHUNKED, new ChunkedWriteHandler())
                        .addLast(HandlerName.HTTP_AGGREGATOR, new HttpObjectAggregator(65535))
                        .addLast(HandlerName.WEB_SOCKET_AGGREGATOR, new WebSocketFrameAggregator(65535))
                        .addLast(HandlerName.AUTH_HANDLER, authHandler)
                        .addLast(HandlerName.WEB_SOCKET_PROTOCOL_HANDLER, new WebSocketServerProtocolHandler(path, true))
                        .addLast(HandlerName.WEB_SOCKET_BYTE_BUF_DECODER, decoder)
                        .addLast(HandlerName.WEB_SOCKET_BYTE_BUF_ENCODER, encoder)
                        .addLast(HandlerName.IDLE_TIMEOUT_HANDLER, new IdleTimeoutHandler(idleTimeout.toMillis(), TimeUnit.MILLISECONDS))
                        .addLast(HandlerName.LOGGER_HANDLER, loggerHandler)
                        .addLast(HandlerName.CHAT_SERVER_HANDLER, chatServerHandler);
            }
        };
    }

}
