package io.leego.chat.server.handle;

import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author Yihleego
 */
public class ReadIdleHandler extends IdleStateHandler {

    public ReadIdleHandler(long timeout, TimeUnit unit) {
        super(timeout, 0L, 0L, unit);
    }

}
