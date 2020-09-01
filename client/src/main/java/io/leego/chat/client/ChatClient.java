package io.leego.chat.client;

import io.leego.chat.HandlerName;
import io.leego.chat.client.handler.ChatClientHandler;
import io.leego.chat.handler.WriteIdleHandler;
import io.leego.chat.handler.codec.ByteBufBoxDecoder;
import io.leego.chat.handler.codec.ByteBufBoxEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author Yihleego
 */
public class ChatClient {
    private static final Logger logger = LoggerFactory.getLogger(ChatClient.class);
    private final Duration idleTimeout;
    private final String host;
    private final Integer port;
    private final ChatClientHandler chatClientHandler;
    private EventLoopGroup group;
    private final MessageToMessageDecoder<?> decoder;
    private final MessageToMessageEncoder<?> encoder;

    public ChatClient(String host, Integer port, Duration idleTimeout, ChatClientHandler chatClientHandler) {
        this.host = host;
        this.port = port;
        this.idleTimeout = idleTimeout;
        this.chatClientHandler = chatClientHandler;
        this.decoder = new ByteBufBoxDecoder();
        this.encoder = new ByteBufBoxEncoder();
    }

    public void start() {
        group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline()
                                .addLast(HandlerName.SOCKET_DECODER, decoder)
                                .addLast(HandlerName.SOCKET_ENCODER, encoder)
                                .addLast(HandlerName.WRITE_IDLE_HANDLER, new WriteIdleHandler(idleTimeout.toMillis(), TimeUnit.MILLISECONDS))
                                .addLast(HandlerName.CHAT_CLIENT_HANDLER, chatClientHandler);
                    }
                });
        try {
            bootstrap.connect(host, port).sync();
        } catch (InterruptedException e) {
            logger.error("", e);
            Thread.currentThread().interrupt();
            stop();
        }
    }

    public void stop() {
        if (group != null) {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                logger.error("", e);
                Thread.currentThread().interrupt();
            }
        }
    }

}

