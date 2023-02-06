package io.leego.chat.server.handler;

import io.leego.chat.core.Box;
import io.leego.chat.server.handler.codec.WebSocketProtobufDecoder;
import io.leego.chat.server.handler.codec.WebSocketProtobufEncoder;
import io.leego.security.SecurityManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author Leego Yih
 */
@ChannelHandler.Sharable
public class ChatServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    protected final int rawPort;
    protected final int wsPort;
    protected final String wsPath;
    protected final Duration authTimeout;
    protected final Duration idleTimeout;
    protected final ChatServerHandler chatServerHandler;
    protected final SecurityManager securityManager;
    protected final ProtobufVarint32LengthFieldPrepender protobufVarint32LengthFieldPrepender;
    protected final ProtobufDecoder protobufDecoder;
    protected final ProtobufEncoder protobufEncoder;
    protected final WebSocketProtobufDecoder webSocketProtobufDecoder;
    protected final WebSocketProtobufEncoder webSocketProtobufEncoder;

    public ChatServerChannelInitializer(
            int rawPort, int wsPort, String wsPath, Duration authTimeout, Duration idleTimeout,
            ChatServerHandler chatServerHandler, SecurityManager securityManager) {
        this.rawPort = rawPort;
        this.wsPort = wsPort;
        this.wsPath = wsPath;
        this.authTimeout = authTimeout;
        this.idleTimeout = idleTimeout;
        this.chatServerHandler = chatServerHandler;
        this.securityManager = securityManager;
        this.protobufVarint32LengthFieldPrepender = new ProtobufVarint32LengthFieldPrepender();
        this.protobufDecoder = new ProtobufDecoder(Box.getDefaultInstance());
        this.protobufEncoder = new ProtobufEncoder();
        this.webSocketProtobufDecoder = new WebSocketProtobufDecoder(Box.getDefaultInstance());
        this.webSocketProtobufEncoder = new WebSocketProtobufEncoder();
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        dispatch(channel, channel.localAddress().getPort());
    }

    protected void dispatch(SocketChannel channel, int port) {
        if (port == rawPort) {
            raw(channel);
        } else if (port == wsPort) {
            ws(channel);
        } else {
            // Cannot happen
            channel.close();
        }
    }

    protected void raw(SocketChannel channel) {
        channel.pipeline().addLast(
                new ProtobufVarint32FrameDecoder(), // Unsharable (e.g. Fragmented Packets)
                protobufDecoder,
                protobufVarint32LengthFieldPrepender,
                protobufEncoder,
                new AuthHandler(authTimeout.toNanos(), TimeUnit.NANOSECONDS, securityManager::get),
                new IdleStateHandler(idleTimeout.toNanos(), 0L, 0L, TimeUnit.NANOSECONDS),
                chatServerHandler);
    }

    protected void ws(SocketChannel channel) {
        channel.pipeline().addLast(
                new HttpServerCodec(),
                new ChunkedWriteHandler(),
                new HttpObjectAggregator(64 << 10),
                new WebSocketServerProtocolHandler(wsPath, true),
                webSocketProtobufDecoder,
                webSocketProtobufEncoder,
                new AuthHandler(authTimeout.toNanos(), TimeUnit.NANOSECONDS, securityManager::get),
                new IdleStateHandler(idleTimeout.toNanos(), 0L, 0L, TimeUnit.NANOSECONDS),
                chatServerHandler);
    }
}
