package io.leego.chat.manager;

import io.leego.chat.config.ChatProperties;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Leego Yih
 */
@Component
public class GroupManager {
    private final StringRedisTemplate redisTemplate;
    private final SetOperations<String, String> setOps;
    private final ChatProperties properties;

    public GroupManager(RedisConnectionFactory connectionFactory, ChatProperties properties) {
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

    public void addMember(Long groupId, Long userId) {
        setOps.add(properties.getMember().getKey() + groupId, Long.toString(userId));
    }

    public void addMembers(Long groupId, Set<Long> userIds) {
        setOps.add(properties.getMember().getKey() + groupId, userIds.stream().map(i -> Long.toString(i)).toArray(String[]::new));
    }

    public void removeMember(Long groupId, Long userId) {
        setOps.remove(properties.getMember().getKey() + groupId, Long.toString(userId));
    }

    public void removeAllMembers(Long groupId) {
        redisTemplate.delete(properties.getMember().getKey() + groupId);
    }
}
