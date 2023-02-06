package io.leego.chat.cluster.handler;

import io.leego.chat.server.handler.ChatServerChannelInitializer;
import io.leego.chat.server.handler.ChatServerHandler;
import io.leego.security.SecurityManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author Leego Yih
 */
@ChannelHandler.Sharable
public class ClusterDiscoveryServerChannelInitializer extends ChatServerChannelInitializer {
    private final int discoveryPort;
    private final ClusterDiscoveryServerHandler clusterDiscoveryServerHandler;

    public ClusterDiscoveryServerChannelInitializer(
            int discoveryPort, int rawPort, int wsPort, String wsPath, Duration authTimeout, Duration idleTimeout,
            ChatServerHandler chatServerHandler, ClusterDiscoveryServerHandler clusterDiscoveryServerHandler, SecurityManager securityManager) {
        super(rawPort, wsPort, wsPath, authTimeout, idleTimeout, chatServerHandler, securityManager);
        this.discoveryPort = discoveryPort;
        this.clusterDiscoveryServerHandler = clusterDiscoveryServerHandler;
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        int port = channel.localAddress().getPort();
        if (port == discoveryPort) {
            discovery(channel);
        } else {
            dispatch(channel, port);
        }
    }

    protected void discovery(SocketChannel channel) {
        channel.pipeline().addLast(
                new ProtobufVarint32FrameDecoder(), // Unsharable (e.g. Fragmented Packets)
                protobufDecoder,
                protobufVarint32LengthFieldPrepender,
                protobufEncoder,
                new IdleStateHandler(5L, 0L, 0L, TimeUnit.SECONDS),
                clusterDiscoveryServerHandler);
    }
}
