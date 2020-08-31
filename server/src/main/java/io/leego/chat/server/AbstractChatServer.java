package io.leego.chat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yihleego
 */
public abstract class AbstractChatServer implements ChatServer {
    private static final Logger logger = LoggerFactory.getLogger(AbstractChatServer.class);
    public static final int INIT = 0;
    public static final int STARTING = 1;
    public static final int RUNNING = 2;
    public static final int STOPPING = 3;
    public static final int STOPPED = 4;
    private final int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private volatile int status = INIT;

    public AbstractChatServer(Integer port) {
        this.port = port;
    }

    @Override
    public void start() {
        if (status != INIT) {
            return;
        }
        synchronized (this) {
            if (status != INIT) {
                return;
            }
            status = STARTING;
            run();
            status = RUNNING;
            logger.info("Server is running on port(s): {}", port);
        }
    }

    @Override
    public void stop() {
        if (status != RUNNING
                || bossGroup == null
                || workerGroup == null) {
            return;
        }
        synchronized (this) {
            if (status != RUNNING) {
                return;
            }
            status = STOPPING;
            try {
                bossGroup.shutdownGracefully().sync();
                workerGroup.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                logger.error("Failed to stop", e);
                Thread.currentThread().interrupt();
            }
            status = STOPPED;
            logger.info("Server is stopped");
        }
    }

    protected void run() {
        if (Epoll.isAvailable()) {
            logger.info("Epoll is available");
            epoll();
        } else {
            logger.info("Nio is available");
            nio();
        }
    }

    private void nio() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(getChannelInitializer());
        try {
            ChannelFuture future = bootstrap.bind(port).sync();
            if (!future.isSuccess()) {
                logger.error("Failed to bind on port(s): {}", port);
            }
        } catch (InterruptedException e) {
            logger.error("Failed to run server on port(s): {}", port, e);
            Thread.currentThread().interrupt();
        }
    }

    private void epoll() {
        bossGroup = new EpollEventLoopGroup();
        workerGroup = new EpollEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(EpollServerSocketChannel.class)
                .option(EpollChannelOption.SO_REUSEPORT, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(getChannelInitializer());
        try {
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            logger.info("The number of available processors is {}", availableProcessors);
            for (int i = 0; i < availableProcessors; i++) {
                ChannelFuture future = bootstrap.bind(port).sync();
                if (!future.isSuccess()) {
                    logger.error("Failed to bind on port(s): {}", port);
                }
            }
        } catch (InterruptedException e) {
            logger.error("Failed to run server on port(s): {}", port, e);
            Thread.currentThread().interrupt();
        }
    }

    protected abstract ChannelInitializer<SocketChannel> getChannelInitializer();

}
