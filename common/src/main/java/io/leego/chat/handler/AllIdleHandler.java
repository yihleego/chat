package io.leego.chat.handler;

import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author Yihleego
 */
public class AllIdleHandler extends IdleStateHandler {

    public AllIdleHandler(long timeout, TimeUnit unit) {
        super(0L, 0L, timeout, unit);
    }

}
