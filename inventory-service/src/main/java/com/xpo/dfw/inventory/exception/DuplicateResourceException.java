package com.xpo.dfw.inventory.exception;

/**
 * Thrown when attempting to create an inventory item whose SKU already
 * exists. Translated to a {@code 409 Conflict} by {@link GlobalExceptionHandler}.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
