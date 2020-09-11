package io.leego.chat.util;

/**
 * @author Yihleego
 */
public final class HandlerName {
    public static final String SOCKET_DECODER = "socketDecoder";
    public static final String SOCKET_ENCODER = "socketEncoder";
    public static final String WEB_SOCKET_DECODER = "webSocketDecoder";
    public static final String WEB_SOCKET_ENCODER = "webSocketEncoder";
    public static final String HTTP_CODEC = "httpCodec";
    public static final String HTTP_CHUNKED = "httpChunked";
    public static final String HTTP_AGGREGATOR = "httpAggregator";
    public static final String WEB_SOCKET_AGGREGATOR = "webSocketAggregator";
    public static final String WEB_SOCKET_PROTOCOL_HANDLER = "webSocketProtocolHandler";
    public static final String READ_IDLE_HANDLER = "readIdleHandler";
    public static final String WRITE_IDLE_HANDLER = "writeIdleHandler";
    public static final String ALL_IDLE_HANDLER = "allIdleHandler";
    public static final String AUTH_TIMEOUT_HANDLER = "authTimeoutHandler";
    public static final String AUTH_HANDLER = "authHandler";
    public static final String LOGGER_HANDLER = "loggerHandler";
    public static final String ROUTE_HANDLER = "routeHandler";
    public static final String CHAT_SERVER_HANDLER = "chatServerHandler";
    public static final String CHAT_CLIENT_HANDLER = "chatClientHandler";

    private HandlerName() {
    }
}
