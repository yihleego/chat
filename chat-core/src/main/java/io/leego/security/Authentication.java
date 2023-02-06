package io.leego.security;

/**
 * @author Leego Yih
 */
public record Authentication(
        Long userId,
        String username,
        String nickname,
        String avatar,
        Long deviceId,
        Short deviceType,
        Short clientType,
        String token) {
}
