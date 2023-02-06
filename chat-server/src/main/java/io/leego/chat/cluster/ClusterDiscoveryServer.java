package io.leego.chat.cluster;

import io.leego.chat.server.ChatServer;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Leego Yih
 */
public class ClusterDiscoveryServer extends ChatServer {
    private static final Logger logger = LoggerFactory.getLogger(ClusterDiscoveryServer.class);
    protected final int discoveryPort;

    public ClusterDiscoveryServer(
            int threads, int discoveryPort, int rawPort, int wsPort, String wsPath,
            ChannelInitializer<SocketChannel> channelInitializer) {
        super(threads, rawPort, wsPort, wsPath, channelInitializer);
        if (!isPort(discoveryPort)) {
            throw new IllegalArgumentException("Invalid port");
        }
        if (discoveryPort == rawPort || discoveryPort == wsPort) {
            throw new IllegalArgumentException("Duplicate ports");
        }
        this.discoveryPort = discoveryPort;
    }

    @Override
    protected void run() {
        super.run();
        ChannelFuture future = bootstrap.bind(discoveryPort).syncUninterruptibly();
        if (future.isSuccess()) {
            logger.info("ChatServer initialized with port(s): {} (discovery)", discoveryPort);
        } else {
            logger.error("Bind port(s) {} (cluster) failed", discoveryPort);
        }
    }
}
