package io.leego.chat.cluster.handler;

import io.leego.chat.core.Box;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * @author Leego Yih
 */
@ChannelHandler.Sharable
public class ClusterRegistryClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final ClusterRegistryClientHandler clusterRegistryClientHandler;
    private final ProtobufVarint32LengthFieldPrepender protobufVarint32LengthFieldPrepender;
    private final ProtobufDecoder protobufDecoder;
    private final ProtobufEncoder protobufEncoder;

    public ClusterRegistryClientChannelInitializer(ClusterRegistryClientHandler clusterRegistryClientHandler) {
        this.clusterRegistryClientHandler = clusterRegistryClientHandler;
        this.protobufVarint32LengthFieldPrepender = new ProtobufVarint32LengthFieldPrepender();
        this.protobufDecoder = new ProtobufDecoder(Box.getDefaultInstance());
        this.protobufEncoder = new ProtobufEncoder();
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        channel.pipeline().addLast(
                new ProtobufVarint32FrameDecoder(), // Unsharable (e.g. Fragmented Packets)
                protobufDecoder,
                protobufVarint32LengthFieldPrepender,
                protobufEncoder,
                clusterRegistryClientHandler);
    }
}
