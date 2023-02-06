package io.leego.chat.manager;

import io.leego.chat.config.ChatServerProperties;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Leego Yih
 */
public class GroupManager {
    private final StringRedisTemplate redisTemplate;
    private final SetOperations<String, String> setOps;
    private final ChatServerProperties properties;

    public GroupManager(RedisConnectionFactory connectionFactory, ChatServerProperties properties) {
        this.redisTemplate = new StringRedisTemplate(connectionFactory);
        this.setOps = redisTemplate.opsForSet();
        this.properties = properties;
    }

    public List<Long> getMembers(Long groupId) {
        Set<String> members = setOps.members(properties.getMember().getKey() + groupId);
        if (CollectionUtils.isEmpty(members)) {
            return Collections.emptyList();
        }
        return members.stream().map(Long::parseLong).toList();
    }

    public boolean isMember(Long groupId, Long userId) {
        Boolean flag = setOps.isMember(properties.getMember().getKey() + groupId, Long.toString(userId));
        return flag != null && flag;
    }

    public int countMembers(Long groupId) {
        Long size = setOps.size(properties.getMember().getKey() + groupId);
        return size != null ? size.intValue() : 0;
    }
}
