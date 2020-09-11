package io.leego.chat.server.handle;

import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author Yihleego
 */
public class WriteIdleHandler extends IdleStateHandler {

    public WriteIdleHandler(long timeout, TimeUnit unit) {
        super(0L, timeout, 0L, unit);
    }

}
