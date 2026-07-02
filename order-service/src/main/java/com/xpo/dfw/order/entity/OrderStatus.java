package com.xpo.dfw.order.entity;

/**
 * Lifecycle status of an {@link Order}.
 */
public enum OrderStatus {

    /** Awaiting manual confirmation (used when {@code app.order.auto-confirm=false}). */
    PENDING,

    /** Stock was successfully reserved and the order is confirmed. */
    CONFIRMED,

    /** One or more items could not be fulfilled; the order was not placed. */
    REJECTED,

    /** The order was cancelled after creation. */
    CANCELLED
}
