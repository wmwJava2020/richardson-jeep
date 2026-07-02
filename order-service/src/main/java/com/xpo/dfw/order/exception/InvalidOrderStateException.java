package com.xpo.dfw.order.exception;

/**
 * Thrown when an order status transition is not permitted (e.g.
 * cancelling an order that has already been cancelled or rejected).
 * Translated to a {@code 409 Conflict} by {@link GlobalExceptionHandler}.
 */
public class InvalidOrderStateException extends RuntimeException {

    public InvalidOrderStateException(String message) {
        super(message);
    }
}
