package io.leego.chat.exception;

import java.io.Serial;

/**
 * Bad Request 400
 *
 * @author Leego Yih
 */
public class BadRequestException extends HttpStatusException {
    @Serial
    private static final long serialVersionUID = 1L;
    public static final int HTTP_STATUS = 400;

    /**
     * Constructs a new exception.
     */
    public BadRequestException() {
        super(HTTP_STATUS);
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public BadRequestException(String message) {
        super(HTTP_STATUS, message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public BadRequestException(String message, Throwable cause) {
        super(HTTP_STATUS, message, cause);
    }

    /**
     * Constructs a new exception with the cause.
     *
     * @param cause the cause.
     */
    public BadRequestException(Throwable cause) {
        super(HTTP_STATUS, cause);
    }
}
