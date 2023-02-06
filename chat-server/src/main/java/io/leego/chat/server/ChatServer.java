package io.leego.chat.server;

import io.leego.chat.util.Executor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Leego Yih
 */
public class ChatServer implements Executor {
    private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);
    protected int status = NEW;
    protected final int rawPort;
    protected final int wsPort;
    protected final String wsPath;
    protected final ServerBootstrap bootstrap;
    protected final EventLoopGroup bossGroup;
    protected final EventLoopGroup workerGroup;

    public ChatServer(
            int threads, int rawPort, int wsPort, String wsPath,
            ChannelInitializer<SocketChannel> channelInitializer) {
        if (!isPort(rawPort) && !isPort(wsPort)) {
            throw new IllegalArgumentException("Invalid ports");
        }
        if (rawPort == wsPort) {
            throw new IllegalArgumentException("Duplicate ports");
        }
        this.rawPort = rawPort;
        this.wsPort = wsPort;
        this.wsPath = wsPath;
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup(threads);
        this.bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(channelInitializer);
    }

    @Override
    public void startup() {
        if (status != NEW) {
            logger.warn("Cannot restart");
            return;
        }
        logger.info("Starting ChatServer");
        synchronized (this) {
            if (status != NEW) {
                return;
            }
            long begin = System.currentTimeMillis();
            run();
            long end = System.currentTimeMillis();
            logger.info("Started ChatServer in {} ms", end - begin);
            status = RUNNING;
        }
    }

    @Override
    public void shutdown() {
        if (status != RUNNING) {
            return;
        }
        logger.info("Stopping ChatServer");
        synchronized (this) {
            if (status != RUNNING) {
                return;
            }
            stop();
            status = TERMINATED;
        }
        logger.info("Stopped ChatServer");
    }

    protected void run() {
        if (isPort(rawPort)) {
            ChannelFuture future = bootstrap.bind(rawPort).syncUninterruptibly();
            if (future.isSuccess()) {
                logger.info("ChatServer initialized with port(s): {} (raw)", rawPort);
            } else {
                logger.error("Bind port(s) {} (raw) failed", rawPort);
            }
        }
        if (isPort(wsPort)) {
            ChannelFuture future = bootstrap.bind(wsPort).syncUninterruptibly();
            if (future.isSuccess()) {
                logger.info("ChatServer initialized with port(s): {} (ws) with path '{}'", wsPort, wsPath);
            } else {
                logger.error("Bind port(s) {} (ws) failed", wsPort);
            }
        }
    }

    protected void stop() {
        if (!bossGroup.isShutdown()) {
            bossGroup.shutdownGracefully().syncUninterruptibly();
            logger.debug("Stopped boss group");
        }
        if (!workerGroup.isShutdown()) {
            workerGroup.shutdownGracefully().syncUninterruptibly();
            logger.debug("Stopped worker group");
        }
    }
}
