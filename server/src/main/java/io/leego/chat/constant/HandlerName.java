package io.leego.chat.constant;

/**
 * @author Yihleego
 */
public final class HandlerName {
    public static final String SOCKET_PROTOBUF_DECODER = "socketProtobufDecoder";
    public static final String SOCKET_PROTOBUF_ENCODER = "socketProtobufEncoder";
    public static final String WEB_SOCKET_PROTOBUF_DECODER = "webSocketProtobufDecoder";
    public static final String WEB_SOCKET_PROTOBUF_ENCODER = "webSocketProtobufEncoder";
    public static final String HTTP_CODEC = "httpCodec";
    public static final String HTTP_CHUNKED = "httpChunked";
    public static final String HTTP_AGGREGATOR = "httpAggregator";
    public static final String WEBSOCKET_AGGREGATOR = "websocketAggregator";
    public static final String WEBSOCKET_PROTOCOL_HANDLER = "websocketProtocolHandler";

    public static final String AUTH_TIMEOUT_HANDLER = "authTimeoutHandler";
    public static final String AUTH_HANDLER = "authenticationHandler";
    public static final String IDLE_TIMEOUT_HANDLER = "idleStateHandler";
    public static final String CHAT_SERVER_HANDLER = "chatServerHandler";

    private HandlerName() {
    }

}
