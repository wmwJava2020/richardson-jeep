package com.xpo.dfw.order.exception;

/**
 * Thrown when Customer Service cannot be reached (circuit open, timeout,
 * or other transport failure) while validating the customer for a new
 * order. Translated to a {@code 503 Service Unavailable} by
 * {@link GlobalExceptionHandler}.
 */
public class CustomerServiceUnavailableException extends RuntimeException {

    public CustomerServiceUnavailableException(String message) {
        super(message);
    }
}
