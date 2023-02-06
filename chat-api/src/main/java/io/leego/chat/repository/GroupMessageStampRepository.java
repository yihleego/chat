package io.leego.chat.repository;

import io.leego.chat.entity.GroupMessageStamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

import java.time.Instant;

/**
 * @author Leego Yih
 */
public interface GroupMessageStampRepository extends JpaRepository<GroupMessageStamp, Long> {

    @Nullable
    GroupMessageStamp findByUserIdAndUserTypeAndDeviceIdAndDeviceTypeAndClientType(Long userId, Short userType, Long deviceId, Short deviceType, Short clientType);

    /** Updates the {@code messageId} and {@code lastTime} of the message stamp with the given ID. */
    @Modifying
    @Query("update GroupMessageStamp set messageId = ?2, lastTime = ?3 where id = ?1 and lastTime < ?3")
    int updateLastTime(Long id, Long messageId, Instant lastTime);

}
