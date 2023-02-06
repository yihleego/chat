package io.leego.chat.manager;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import io.leego.chat.config.ChatServerProperties;
import io.leego.chat.constant.ClientType;
import io.leego.chat.core.Instance;
import io.leego.chat.core.Meta;
import io.leego.chat.util.InetUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Leego Yih
 */
public class ChatManager {
    private final RedisTemplate<String, byte[]> redisTemplate;
    private final ValueOperations<String, byte[]> valueOps;
    private final HashOperations<String, String, byte[]> hashOps;
    private final RedisScript<Void> removeScript;
    private final ChatServerProperties properties;
    private final String localhost;
    private final int node;

    public ChatManager(RedisConnectionFactory connectionFactory, ChatServerProperties properties) {
        InetAddress address = InetUtils.findFirstNonLoopbackAddress();
        if (address == null) {
            throw new RuntimeException("Unknown host");
        }
        this.localhost = address.getHostName();
        RedisTemplate<String, byte[]> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(RedisSerializer.byteArray());
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        redisTemplate.setHashValueSerializer(RedisSerializer.byteArray());
        redisTemplate.afterPropertiesSet();
        this.redisTemplate = redisTemplate;
        this.valueOps = redisTemplate.opsForValue();
        this.hashOps = redisTemplate.opsForHash();
        this.removeScript = new DefaultRedisScript<>("""
                local v = redis.call('GET', KEYS[1])
                if v == ARGV[1] then
                  redis.call('DEL', KEYS[1])
                end""", void.class);
        this.properties = properties;
        if (properties.getCluster().isEnabled()) {
            Long seq = valueOps.increment(properties.getRegistry().getSeq());
            if (seq == null) {
                throw new RuntimeException();
            }
            this.node = seq.intValue();
        } else {
            this.node = 0;
        }
    }

    public final int getNode() {
        return node;
    }

    public List<Instance> getInstances() {
        List<byte[]> values = hashOps.values(properties.getRegistry().getKey());
        if (CollectionUtils.isEmpty(values)) {
            return Collections.emptyList();
        }
        return values.stream()
                .map(o -> parse(o, Instance.parser()))
                .filter(Objects::nonNull)
                .toList();
    }

    public Instance getInstance(int node) {
        byte[] value = hashOps.get(properties.getRegistry().getKey(), Integer.toString(node));
        if (value == null) {
            return null;
        }
        return parse(value, Instance.parser());
    }

    public void setInstance() {
        String host = properties.getDiscovery().getHost();
        Instance instance = Instance.newBuilder()
                .setNode(node)
                .setTimestamp(System.currentTimeMillis())
                .setHost(StringUtils.hasText(host) ? host : localhost)
                .setDiscoveryPort(properties.getDiscovery().getPort())
                .setRawPort(properties.getRaw().getPort())
                .setWsPort(properties.getWs().getPort())
                .build();
        hashOps.put(properties.getRegistry().getKey(), Integer.toString(node), instance.toByteArray());
    }

    public void removeInstance() {
        hashOps.delete(properties.getRegistry().getKey(), Integer.toString(node));
    }

    public boolean isSelf(Instance instance) {
        return instance.getNode() == node;
    }

    public boolean isAlive(Instance instance) {
        return System.currentTimeMillis() - instance.getTimestamp() < properties.getRegistry().getTimeToLive().toMillis();
    }


    public Meta getExclusiveMeta(Long userId) {
        Meta meta = parse(valueOps.get(getExclusiveMetaKey(userId)), Meta.parser());
        return meta == null || meta.getNode() == node ? null : meta;
    }

    public Stream<Meta> getExclusiveMetas(List<Long> userIds) {
        List<byte[]> values = valueOps.multiGet(getExclusiveMetaKeys(userIds));
        if (CollectionUtils.isEmpty(values)) {
            return Stream.empty();
        }
        return values.stream()
                .map(o -> parse(o, Meta.parser()))
                .filter(Objects::nonNull)
                .filter(o -> o.getNode() != node);
    }

    public Meta setExclusiveMeta(Long userId, int channelId) {
        byte[] newMeta = Meta.newBuilder().setNode(node).setUser(userId).setChannel(channelId).build().toByteArray();
        byte[] oldMeta = valueOps.getAndSet(getExclusiveMetaKey(userId), newMeta);
        return parse(oldMeta, Meta.parser());
    }

    public void removeExclusiveMeta(Long userId, int channelId) {
        byte[] oldMeta = Meta.newBuilder().setNode(node).setUser(userId).setChannel(channelId).build().toByteArray();
        redisTemplate.execute(removeScript, List.of(getExclusiveMetaKey(userId)), new Object[]{oldMeta});
    }

    private String getExclusiveMetaKey(Long userId) {
        return properties.getMeta().getKey() + userId;
    }

    private List<String> getExclusiveMetaKeys(List<Long> userIds) {
        String prefix = properties.getMeta().getKey();
        return userIds.stream().map(userId -> prefix + userId).toList();
    }


    public List<Meta> getSharedMeta(Long userId) {
        List<byte[]> values = valueOps.multiGet(getSharedMetaKeys(userId));
        if (CollectionUtils.isEmpty(values)) {
            return Collections.emptyList();
        }
        return values.stream()
                .map(o -> parse(o, Meta.parser()))
                .filter(Objects::nonNull)
                .filter(o -> o.getNode() != node)
                .collect(Collectors.toList());
    }

    public List<Meta> getSharedMetas(List<Long> userIds) {
        List<byte[]> values = valueOps.multiGet(getSharedMetaKeys(userIds));
        if (CollectionUtils.isEmpty(values)) {
            return Collections.emptyList();
        }
        return values.stream()
                .map(o -> parse(o, Meta.parser()))
                .filter(Objects::nonNull)
                .filter(o -> o.getNode() != node)
                .toList();
    }

    public Meta setSharedMeta(Long userId, Integer client, int channelId) {
        byte[] newMeta = Meta.newBuilder().setNode(node).setUser(userId).setClient(client).setChannel(channelId).build().toByteArray();
        byte[] oldMeta = valueOps.getAndSet(getSharedMetaKey(userId, client), newMeta);
        return parse(oldMeta, Meta.parser());
    }

    public void removeSharedMeta(Long userId, Integer client, int channelId) {
        byte[] oldMeta = Meta.newBuilder().setNode(node).setUser(userId).setClient(client).setChannel(channelId).build().toByteArray();
        redisTemplate.execute(removeScript, List.of(getSharedMetaKey(userId, client)), new Object[]{oldMeta});
    }

    private String getSharedMetaKey(Long userId, Integer client) {
        return properties.getMeta().getKey() + userId + "-" + client;
    }

    private List<String> getSharedMetaKeys(Long userId) {
        List<String> keys = new ArrayList<>();
        for (ClientType e : ClientType.values()) {
            keys.add(properties.getMeta().getKey() + userId + "-" + e.getCode());
        }
        return keys;
    }

    private List<String> getSharedMetaKeys(List<Long> userIds) {
        List<String> keys = new ArrayList<>(userIds.size() * ClientType.values().length);
        for (ClientType e : ClientType.values()) {
            for (Long userId : userIds) {
                keys.add(properties.getMeta().getKey() + userId + "-" + e.getCode());
            }
        }
        return keys;
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
