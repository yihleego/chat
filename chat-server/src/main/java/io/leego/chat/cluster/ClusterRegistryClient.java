package io.leego.chat.cluster;

import com.google.protobuf.TextFormat;
import io.leego.chat.client.RegistryClient;
import io.leego.chat.core.Instance;
import io.leego.chat.manager.ChatManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Leego Yih
 */
public class ClusterRegistryClient extends RegistryClient implements ApplicationListener<RegisteredEvent> {
    private static final Logger logger = LoggerFactory.getLogger(ClusterRegistryClient.class);
    protected final Bootstrap bootstrap;
    protected final EventLoopGroup group;
    protected final ConcurrentMap<String, Channel> channels;
    protected List<Instance> instances = Collections.emptyList();

    public ClusterRegistryClient(
            int threads, Duration pullPeriod, Duration pushPeriod,
            ChannelInitializer<SocketChannel> channelInitializer,
            ChatManager chatManager) {
        super(pullPeriod, pushPeriod, chatManager);
        this.group = new NioEventLoopGroup(threads);
        this.bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10 * 1000)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(channelInitializer);
        this.channels = new ConcurrentHashMap<>(64);
    }

    @Override
    protected void run() {
        group.scheduleWithFixedDelay(this::fetch, 0, pullPeriod.toMillis(), TimeUnit.MILLISECONDS);
        group.scheduleWithFixedDelay(this::register, 0, pushPeriod.toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    protected void stop() {
        if (!group.isShutdown()) {
            group.shutdownGracefully().syncUninterruptibly();
            logger.debug("Stopped group");
        }
        unregister();
    }

    @Override
    public void onApplicationEvent(RegisteredEvent event) {
        int node = event.getNode();
        synchronized (this) {
            for (Instance instance : instances) {
                if (instance.getNode() == node) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("The instance is already connected: [{}]", TextFormat.shortDebugString(instance));
                    }
                    return;
                }
            }
            try {
                Instance instance = chatManager.getInstance(node);
                if (instance == null) {
                    logger.error("No instance with the given node [{}]", node);
                    return;
                }
                if (chatManager.isSelf(instance)) {
                    logger.error("Invalid instance [{}]", TextFormat.shortDebugString(instance));
                    return;
                }
                connect(instance.getHost(), instance.getDiscoveryPort());
            } catch (Throwable cause) {
                logger.error("An error occurred while connect to node [{}]", node, cause);
            }
        }
    }

    protected void fetch() {
        synchronized (this) {
            try {
                instances = chatManager.getInstances();
                if (instances.isEmpty()) {
                    return;
                }
                for (Instance instance : instances) {
                    if (chatManager.isSelf(instance)) {
                        continue; // Skip self
                    }
                    if (!chatManager.isAlive(instance)) {
                        logger.warn("The instance is dead: [{}]", TextFormat.shortDebugString(instance));
                        continue;
                    }
                    connect(instance.getHost(), instance.getDiscoveryPort());
                }
            } catch (Throwable cause) {
                logger.error("An error occurred while fetching instances", cause);
            }
        }
    }

    protected void connect(String host, int port) {
        if (group.isShutdown()) {
            return;
        }
        String address = host + ":" + port;
        Channel channel = channels.compute(address, ($, oldChannel) -> {
            if (oldChannel != null && oldChannel.isActive()) {
                return oldChannel;
            }
            logger.info("Connecting to node {}", address);
            ChannelFuture future = bootstrap.connect(host, port).syncUninterruptibly();
            Channel newChannel = future.channel();
            if (newChannel.isActive()) {
                logger.info("Connected to node {}", address);
                newChannel.closeFuture().addListener((listener) -> channels.remove(address, newChannel));
            }
            return newChannel;
        });
        if (!channel.isActive()) {
            logger.error("Connect to node {} failed", address);
            channels.remove(address, channel);
        }
    }
}
