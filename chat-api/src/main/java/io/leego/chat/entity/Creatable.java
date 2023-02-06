package io.leego.chat.entity;

import java.time.Instant;

/**
 * @author Leego Yih
 */
public interface Creatable {

    Instant getCreatedTime();

    void setCreatedTime(Instant createdTime);

}
