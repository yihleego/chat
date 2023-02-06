package io.leego.chat.exception;

import java.io.Serial;

/**
 * Internal Server Error 500
 *
 * @author Leego Yih
 */
public class InternalServerErrorException extends HttpStatusException {
    @Serial
    private static final long serialVersionUID = 1L;
    public static final int HTTP_STATUS = 500;

    /**
     * Constructs a new exception.
     */
    public InternalServerErrorException() {
        super(HTTP_STATUS);
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public InternalServerErrorException(String message) {
        super(HTTP_STATUS, message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public InternalServerErrorException(String message, Throwable cause) {
        super(HTTP_STATUS, message, cause);
    }

    /**
     * Constructs a new exception with the cause.
     *
     * @param cause the cause.
     */
    public InternalServerErrorException(Throwable cause) {
        super(HTTP_STATUS, cause);
    }
}
