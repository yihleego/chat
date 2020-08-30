package io.leego.chat.server.handle;

import io.netty.channel.ChannelHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author Yihleego
 */
@ChannelHandler.Sharable
public class IdleTimeoutHandler extends IdleStateHandler {

    public IdleTimeoutHandler(long timeout, TimeUnit unit) {
        super(timeout, 0L, 0L, unit);
    }

}
