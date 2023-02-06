package io.leego.chat.exception;

import java.io.Serial;

/**
 * Forbidden 403
 *
 * @author Leego Yih
 */
public class ForbiddenException extends HttpStatusException {
    @Serial
    private static final long serialVersionUID = 1L;
    public static final int HTTP_STATUS = 403;

    /**
     * Constructs a new exception.
     */
    public ForbiddenException() {
        super(HTTP_STATUS);
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public ForbiddenException(String message) {
        super(HTTP_STATUS, message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public ForbiddenException(String message, Throwable cause) {
        super(HTTP_STATUS, message, cause);
    }

    /**
     * Constructs a new exception with the cause.
     *
     * @param cause the cause.
     */
    public ForbiddenException(Throwable cause) {
        super(HTTP_STATUS, cause);
    }
}
