package io.leego.chat.exception;

import java.io.Serial;

/**
 * Unauthorized 401
 *
 * @author Leego Yih
 */
public class UnauthorizedException extends HttpStatusException {
    @Serial
    private static final long serialVersionUID = 1L;
    public static final int HTTP_STATUS = 401;

    /**
     * Constructs a new exception.
     */
    public UnauthorizedException() {
        super(HTTP_STATUS);
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public UnauthorizedException(String message) {
        super(HTTP_STATUS, message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(HTTP_STATUS, message, cause);
    }

    /**
     * Constructs a new exception with the cause.
     *
     * @param cause the cause.
     */
    public UnauthorizedException(Throwable cause) {
        super(HTTP_STATUS, cause);
    }
}
