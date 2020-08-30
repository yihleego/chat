package io.leego.chat.server;

import io.leego.chat.constant.HandlerName;
import io.leego.chat.server.handle.AuthHandler;
import io.leego.chat.server.handle.ChatServerHandler;
import io.leego.chat.server.handle.codec.WebSocketProtobufDecoder;
import io.leego.chat.server.handle.codec.WebSocketProtobufEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author Yihleego
 */
public class WebSocketChatServer extends AbstractChatServer {
    private final AuthHandler authHandler;
    private final WebSocketProtobufDecoder webSocketProtobufDecoder;
    private final WebSocketProtobufEncoder webSocketProtobufEncoder;
    private final ChatServerHandler chatServerHandler;
    private final String path;
    private final Duration idleTimeout;

    public WebSocketChatServer(Integer port, String path, Duration idleTimeout, Duration authTimeout, AuthHandler authHandler, ChatServerHandler chatServerHandler) {
        super(port);
        this.authHandler = authHandler;
        this.webSocketProtobufDecoder = new WebSocketProtobufDecoder();
        this.webSocketProtobufEncoder = new WebSocketProtobufEncoder();
        this.chatServerHandler = chatServerHandler;
        this.path = path;
        this.idleTimeout = idleTimeout;
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
                        .addLast(HandlerName.WEBSOCKET_AGGREGATOR, new WebSocketFrameAggregator(65535))
                        .addLast(HandlerName.AUTH_HANDLER, authHandler)
                        .addLast(HandlerName.WEBSOCKET_PROTOCOL_HANDLER, new WebSocketServerProtocolHandler(path != null ? path : "", true))
                        .addLast(HandlerName.WEB_SOCKET_PROTOBUF_DECODER, webSocketProtobufDecoder)
                        .addLast(HandlerName.WEB_SOCKET_PROTOBUF_ENCODER, webSocketProtobufEncoder)
                        .addLast(HandlerName.IDLE_TIMEOUT_HANDLER, new IdleStateHandler(idleTimeout.toMillis(), 0L, 0L, TimeUnit.MILLISECONDS))
                        .addLast(HandlerName.CHAT_SERVER_HANDLER, chatServerHandler);

            }
        };
    }

}
