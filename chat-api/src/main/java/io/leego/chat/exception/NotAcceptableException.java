package io.leego.chat.exception;

import java.io.Serial;

/**
 * Not Acceptable 406
 *
 * @author Leego Yih
 */
public class NotAcceptableException extends HttpStatusException {
    @Serial
    private static final long serialVersionUID = 1L;
    public static final int HTTP_STATUS = 406;

    /**
     * Constructs a new exception.
     */
    public NotAcceptableException() {
        super(HTTP_STATUS);
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public NotAcceptableException(String message) {
        super(HTTP_STATUS, message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public NotAcceptableException(String message, Throwable cause) {
        super(HTTP_STATUS, message, cause);
    }

    /**
     * Constructs a new exception with the cause.
     *
     * @param cause the cause.
     */
    public NotAcceptableException(Throwable cause) {
        super(HTTP_STATUS, cause);
    }
}
