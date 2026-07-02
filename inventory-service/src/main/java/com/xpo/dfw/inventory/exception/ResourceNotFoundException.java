package com.xpo.dfw.inventory.exception;

/**
 * Thrown when a requested {@code InventoryItem} cannot be found.
 * Translated to a {@code 404 Not Found} by {@link GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
