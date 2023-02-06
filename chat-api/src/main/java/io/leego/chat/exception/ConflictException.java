package io.leego.chat.exception;

import java.io.Serial;

/**
 * Conflict 409
 *
 * @author Leego Yih
 */
public class ConflictException extends HttpStatusException {
    @Serial
    private static final long serialVersionUID = 1L;
    public static final int HTTP_STATUS = 409;

    /**
     * Constructs a new exception.
     */
    public ConflictException() {
        super(HTTP_STATUS);
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public ConflictException(String message) {
        super(HTTP_STATUS, message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public ConflictException(String message, Throwable cause) {
        super(HTTP_STATUS, message, cause);
    }

    /**
     * Constructs a new exception with the cause.
     *
     * @param cause the cause.
     */
    public ConflictException(Throwable cause) {
        super(HTTP_STATUS, cause);
    }
}
