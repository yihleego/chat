package io.leego.chat.controller.advice;

import io.leego.chat.exception.HttpStatusException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * @author Leego Yih
 */
@RestControllerAdvice
public class GlobalControllerAdvice {
    private static final Logger logger = LoggerFactory.getLogger(GlobalControllerAdvice.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public void handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.error("", e);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public void handleBindException(BindException e) {
        logger.error("", e);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public void handleHttpMessageConversionException(HttpMessageConversionException e) {
        logger.error("", e);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public void handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        logger.error("", e);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public void handleException(Exception e) {
        logger.error("", e);
    }

    @ExceptionHandler
    public void handleHttpStatusException(HttpStatusException e, HttpServletResponse response) {
        logger.error("", e);
        response.setStatus(e.getStatus());
    }

    @ExceptionHandler
    public void handleHttpException(HttpStatusCodeException e, HttpServletResponse response) {
        logger.error("", e);
        response.setStatus(e.getStatusCode().value());
    }
}
