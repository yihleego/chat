package io.leego.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.function.Function;

/**
 * @author Leego Yih
 */
public final class SecurityUtils {
    private static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);
    private static final SecureRandom random = new SecureRandom();
    private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    private static final ThreadLocal<Authentication> cache = new InheritableThreadLocal<>();
    private static final ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.USE_GETTERS_AS_SETTERS)
            .enable(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS)
            .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
            .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
            .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .addModules(new Jdk8Module())
            .build();

    private SecurityUtils() {
    }

    public static void set(Authentication a) {
        if (a != null) {
            cache.set(a);
        }
    }

    public static void set(String s) {
        Authentication a = deserialize(s);
        if (a != null) {
            cache.set(a);
        }
    }

    public static void remove() {
        cache.remove();
    }

    public static boolean isAuthenticated() {
        return cache.get() != null;
    }

    public static Authentication get() {
        return cache.get();
    }

    public static Long getUserId() {
        return getValue(Authentication::userId);
    }

    public static Long getDeviceId() {
        return getValue(Authentication::deviceId);
    }

    public static Short getDeviceType() {
        return getValue(Authentication::deviceType);
    }

    public static Short getClientType() {
        return getValue(Authentication::clientType);
    }

    public static String getToken() {
        return getValue(Authentication::token);
    }

    public static <T> T getValue(Function<Authentication, T> getter) {
        Authentication a = cache.get();
        if (a == null) {
            return null;
        }
        return getter.apply(a);
    }

    public static String serialize(Authentication a) {
        if (a == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(a);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize authentication", e);
        }
        return null;
    }

    public static Authentication deserialize(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(s, Authentication.class);
        } catch (JsonProcessingException e) {
            logger.error("Failed to deserialize authentication", e);
        }
        return null;
    }

    public static String randomSalt() {
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return encoder.encodeToString(salt);
    }

    public static String randomToken() {
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        bytes[6] &= 0x0f;
        bytes[6] |= 0x40;
        bytes[8] &= 0x3f;
        bytes[8] |= 0x80;
        return encoder.encodeToString(bytes);
    }
}
