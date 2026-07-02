package com.xpo.dfw.customer.exception;

/**
 * Thrown when a requested {@code Customer} cannot be found. Translated to
 * a {@code 404 Not Found} by {@link GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
