package io.leego.chat.exception;

import java.io.Serial;

/**
 * Base class for exceptions based on a http status code.
 * <p>This class is {@code abstract} to force the developer to extend the class. </p>
 *
 * @author Leego Yih
 */
public abstract class HttpStatusException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    protected final int status;

    /**
     * Constructs a new exception with the status code only.
     *
     * @param statusCode the status code.
     */
    public HttpStatusException(int statusCode) {
        super();
        this.status = statusCode;
    }

    /**
     * Constructs a new exception with the status code and specified detail message.
     *
     * @param statusCode the status code.
     * @param message    the detail message.
     */
    public HttpStatusException(int statusCode, String message) {
        super(message);
        this.status = statusCode;
    }

    /**
     * Constructs a new exception with the status code, specified detail message, and cause.
     *
     * @param statusCode the status code.
     * @param message    the detail message.
     * @param cause      the cause.
     */
    public HttpStatusException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.status = statusCode;
    }

    /**
     * Constructs a new exception with the status code and cause.
     *
     * @param statusCode the status code.
     * @param cause      the cause.
     */
    public HttpStatusException(int statusCode, Throwable cause) {
        super(cause);
        this.status = statusCode;
    }

    public int getStatus() {
        return status;
    }
}
