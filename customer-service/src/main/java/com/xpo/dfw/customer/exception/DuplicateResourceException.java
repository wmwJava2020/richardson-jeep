package com.xpo.dfw.customer.exception;

/**
 * Thrown when attempting to create a customer whose email already exists.
 * Translated to a {@code 409 Conflict} by {@link GlobalExceptionHandler}.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
