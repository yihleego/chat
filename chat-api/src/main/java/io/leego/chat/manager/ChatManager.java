package io.leego.chat.manager;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import io.leego.chat.config.ChatProperties;
import io.leego.chat.core.Instance;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Leego Yih
 */
@Component
public class ChatManager implements InitializingBean, DisposableBean {
    private final HashOperations<String, String, byte[]> hashOps;
    private final ChatProperties properties;
    private final ScheduledExecutorService executor;
    private final AtomicInteger counter;
    private Instance[] instances;

    public ChatManager(RedisConnectionFactory connectionFactory, ChatProperties properties) {
        RedisTemplate<String, byte[]> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(RedisSerializer.byteArray());
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        redisTemplate.setHashValueSerializer(RedisSerializer.byteArray());
        redisTemplate.afterPropertiesSet();
        this.hashOps = redisTemplate.opsForHash();
        this.properties = properties;
        this.counter = new AtomicInteger();
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void afterPropertiesSet() {
        executor.scheduleWithFixedDelay(() -> {
            List<byte[]> values = hashOps.values(properties.getRegistry().getKey());
            if (CollectionUtils.isEmpty(values)) {
                return;
            }
            long t = System.currentTimeMillis() - properties.getRegistry().getTimeToLive().toMillis();
            instances = values.stream()
                    .map(o -> parse(o, Instance.parser()))
                    .filter(Objects::nonNull)
                    .filter(o -> t < o.getTimestamp())
                    .toArray(Instance[]::new);
        }, 0, 3, TimeUnit.MINUTES);
    }

    @Override
    public void destroy() {
        instances = null;
        executor.shutdown();
    }

    public Instance nextInstance() {
        Instance[] a = instances;
        if (a == null) {
            return null;
        }
        int len = a.length;
        if (len == 1) {
            return a[0];
        }
        return a[Math.abs(counter.getAndIncrement()) % len];
    }

    private <T> T parse(byte[] bytes, Parser<T> parser) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return parser.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            return null;
        }
    }
}
