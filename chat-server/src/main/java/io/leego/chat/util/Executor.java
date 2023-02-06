package io.leego.chat.util;

/**
 * @author Leego Yih
 */
public interface Executor {
    int NEW = 0;
    int RUNNING = 1;
    int TERMINATED = 2;

    void startup();

    void shutdown();

    default boolean isPort(int port) {
        return port > 0 && port <= 65535;
    }
}
