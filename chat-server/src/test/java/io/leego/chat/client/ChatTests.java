package io.leego.chat.client;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import io.leego.chat.constant.Codes;
import io.leego.chat.core.Box;
import io.leego.chat.core.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;

/**
 * @author Leego Yih
 */
public class ChatTests {
    private static final Logger logger = LoggerFactory.getLogger(ChatTests.class);
    private ChatClient chatClient;
    private String token = "XMNhiAzlRM6ccFBGZqAgtg";

    @BeforeEach
    public void before() {
        chatClient = new ChatClient("localhost", 10000);
        chatClient.startup();
    }

    @AfterEach
    public void after() {
        chatClient.shutdown();
    }

    @Test
    public void testMessageSend() throws InterruptedException {
        Message message = Message.newBuilder()
                .setId(99)
                .setSender(1)
                .setRecipient(2)
                .build();
        Box box = Box.newBuilder()
                .setCode(Codes.MESSAGE_SEND_NOTIFY)
                .setData(Any.newBuilder().setValue(message.toByteString()))
                .build();
        chatClient.send(box, (ctx, res) -> {
            if (res.getCode() == Codes.MESSAGE_TAKE_OFFLINE_PUSH) {
                logger.debug("OK");
            } else {
                Assertions.fail("Error");
            }
        });
    }

    public class ChatClient {
        private final String host;
        private final Integer port;
        private final Bootstrap bootstrap;
        private final EventLoopGroup group;
        private Channel channel;

        public ChatClient(String host, Integer port) {
            this.host = host;
            this.port = port;
            this.group = new NioEventLoopGroup();
            this.bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(
                                    new ProtobufVarint32FrameDecoder(),
                                    new ProtobufDecoder(Box.getDefaultInstance()),
                                    new ProtobufVarint32LengthFieldPrepender(),
                                    new ProtobufEncoder());
                        }
                    });
        }

        public void startup() {
            ChannelFuture future = bootstrap.connect(host, port);
            channel = future.channel();
        }

        public void shutdown() {
            group.shutdownGracefully().syncUninterruptibly();
        }

        public void send(Object msg, BiConsumer<ChannelHandlerContext, Box> handler) throws InterruptedException {
            CountDownLatch latch = new CountDownLatch(1);
            channel.pipeline().addLast(new SimpleChannelInboundHandler<Box>() {
                volatile boolean authenticated;

                @Override
                protected void channelRead0(ChannelHandlerContext ctx, Box box) {
                    if (authenticated) {
                        handler.accept(ctx, box);
                        ctx.close();
                        latch.countDown();
                    } else if (box.getCode() == Codes.AUTHENTICATED) {
                        logger.debug("Authenticated");
                        authenticated = true;
                        ctx.writeAndFlush(msg);
                    } else {
                        Assertions.fail("Non-authenticated");
                        ctx.close();
                        latch.countDown();
                    }
                }
            });
            channel.writeAndFlush(Box.newBuilder()
                    .setCode(Codes.AUTHENTICATION)
                    .setData(Any.newBuilder().setValue(ByteString.copyFrom(token, StandardCharsets.UTF_8)))
                    .build());
            latch.await();
        }
    }
}
