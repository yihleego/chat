package io.leego.chat.exception;

import java.io.Serial;

/**
 * Not Found 404
 *
 * @author Leego Yih
 */
public class NotFoundException extends HttpStatusException {
    @Serial
    private static final long serialVersionUID = 1L;
    public static final int HTTP_STATUS = 404;

    /**
     * Constructs a new exception.
     */
    public NotFoundException() {
        super(HTTP_STATUS);
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public NotFoundException(String message) {
        super(HTTP_STATUS, message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public NotFoundException(String message, Throwable cause) {
        super(HTTP_STATUS, message, cause);
    }

    /**
     * Constructs a new exception with the cause.
     *
     * @param cause the cause.
     */
    public NotFoundException(Throwable cause) {
        super(HTTP_STATUS, cause);
    }
}
