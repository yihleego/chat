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
public class ContactManager {
    private final StringRedisTemplate redisTemplate;
    private final SetOperations<String, String> setOps;
    private final ChatProperties properties;

    public ContactManager(RedisConnectionFactory connectionFactory, ChatProperties properties) {
        this.redisTemplate = new StringRedisTemplate(connectionFactory);
        this.setOps = redisTemplate.opsForSet();
        this.properties = properties;
    }

    public List<Long> getContacts(Long sender) {
        Set<String> contacts = setOps.members(properties.getContact().getKey() + sender);
        if (CollectionUtils.isEmpty(contacts)) {
            return Collections.emptyList();
        }
        return contacts.stream().map(Long::parseLong).toList();
    }

    public boolean isContact(Long sender, Long recipient) {
        Boolean flag = setOps.isMember(properties.getContact().getKey() + sender, Long.toString(recipient));
        return flag != null && flag;
    }

    public int countContacts(Long sender) {
        Long size = setOps.size(properties.getContact().getKey() + sender);
        return size != null ? size.intValue() : 0;
    }

    public void addContact(Long sender, Long recipient) {
        setOps.add(properties.getContact().getKey() + sender, Long.toString(recipient));
    }

    public void removeContact(Long sender, Long recipient) {
        setOps.remove(properties.getContact().getKey() + sender, Long.toString(recipient));
    }

    public void removeAllContacts(Long sender) {
        redisTemplate.delete(properties.getContact().getKey() + sender);
    }
}
