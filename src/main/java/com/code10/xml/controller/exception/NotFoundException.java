package com.code10.xml.controller.exception;

/**
 * Custom exception.
 * Gets mapped to {@link org.springframework.http.HttpStatus#NOT_FOUND} when caught in
 * {@link com.code10.xml.controller.exception.resolver.ExceptionResolver}.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException() {
    }

    public NotFoundException(String message) {
        super(message);
    }
}
