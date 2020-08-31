package io.leego.chat.constant;

/**
 * @author Yihleego
 */
public final class HandlerName {
    public static final String SOCKET_PROTOBUF_DECODER = "socketProtobufDecoder";
    public static final String SOCKET_PROTOBUF_ENCODER = "socketProtobufEncoder";
    public static final String WEB_SOCKET_PROTOBUF_DECODER = "webSocketProtobufDecoder";
    public static final String WEB_SOCKET_PROTOBUF_ENCODER = "webSocketProtobufEncoder";
    public static final String WEB_SOCKET_BYTE_BUF_DECODER = "webSocketByteBufDecoder";
    public static final String WEB_SOCKET_BYTE_BUF_ENCODER = "webSocketByteBufEncoder";
    public static final String HTTP_CODEC = "httpCodec";
    public static final String HTTP_CHUNKED = "httpChunked";
    public static final String HTTP_AGGREGATOR = "httpAggregator";
    public static final String WEB_SOCKET_AGGREGATOR = "webSocketAggregator";
    public static final String WEB_SOCKET_PROTOCOL_HANDLER = "webSocketProtocolHandler";
    public static final String AUTH_TIMEOUT_HANDLER = "authTimeoutHandler";
    public static final String AUTH_HANDLER = "authHandler";
    public static final String IDLE_TIMEOUT_HANDLER = "idleTimeoutHandler";
    public static final String LOGGER_HANDLER = "loggerHandler";
    public static final String CHAT_SERVER_HANDLER = "chatServerHandler";

    private HandlerName() {
    }

}
