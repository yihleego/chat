package io.leego.chat.cluster;

import org.springframework.context.ApplicationEvent;

/**
 * @author Leego Yih
 */
public class RegisteredEvent extends ApplicationEvent {
    private final int node;

    public RegisteredEvent(int node) {
        super(node);
        this.node = node;
    }

    public int getNode() {
        return node;
    }
}
