package io.leego.chat.entity;

import java.time.Instant;

/**
 * @author Leego Yih
 */
public interface Updatable {

    Instant getUpdatedTime();

    void setUpdatedTime(Instant updatedTime);

}
