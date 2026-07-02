package com.xpo.dfw.order.dto.client;

import lombok.*;

/**
 * Mirrors Inventory Service's {@code StockReservationResponse}, returned
 * by both the {@code /availability/{sku}} and {@code /reserve} endpoints.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockReservationResponse {

    private String sku;
    private boolean reserved;
    private Integer remainingQuantity;
    private String message;
}
