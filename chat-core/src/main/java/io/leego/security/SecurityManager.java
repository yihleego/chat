package io.leego.security;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

/**
 * @author Leego Yih
 */
public class SecurityManager {
    private final StringRedisTemplate redisTemplate;
    private final ValueOperations<String, String> valueOps;

    public SecurityManager(RedisConnectionFactory connectionFactory) {
        this.redisTemplate = new StringRedisTemplate(connectionFactory);
        this.valueOps = redisTemplate.opsForValue();
    }

    public Authentication get(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        String v = valueOps.get(SecurityKeys.SESSION_KEY + token);
        if (v == null) {
            return null;
        }
        return SecurityUtils.deserialize(v);
    }

    public void set(String token, Authentication a, Duration timeout) {
        valueOps.set(SecurityKeys.SESSION_KEY + token, SecurityUtils.serialize(a), timeout);
    }

    public void refresh(String token, Duration timeout) {
        redisTemplate.expire(SecurityKeys.SESSION_KEY + token, timeout);
    }

    public boolean remove(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        Boolean b = redisTemplate.delete(SecurityKeys.SESSION_KEY + token);
        return b != null && b;
    }
}
