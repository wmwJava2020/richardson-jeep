package com.xpo.dfw.notification.exception;

/**
 * Thrown when a notification fails simulated delivery (e.g. because its
 * channel is disabled via configuration). Translated to a
 * {@code 502 Bad Gateway} by {@link GlobalExceptionHandler}.
 */
public class NotificationDeliveryException extends RuntimeException {

    public NotificationDeliveryException(String message) {
        super(message);
    }
}
