package com.xpo.dfw.order.exception;

/**
 * Thrown when a requested {@code Order} (or referenced {@code Customer})
 * cannot be found. Translated to a {@code 404 Not Found} by
 * {@link GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
