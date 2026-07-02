package com.xpo.dfw.inventory.exception;

/**
 * Thrown when a stock reservation is requested for a quantity greater than
 * what is currently available. Translated to a {@code 409 Conflict} by
 * {@link GlobalExceptionHandler}.
 */
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }
}
