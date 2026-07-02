package com.xpo.dfw.order.dto.client;

import lombok.*;

/**
 * Mirrors Inventory Service's {@code StockReservationRequest}, sent by
 * {@link com.xpo.dfw.order.client.InventoryClient} when reserving stock
 * for a confirmed order.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockReservationRequest {

    private String sku;
    private Integer quantity;

    /** Correlates this reservation with the originating order number. */
    private String referenceId;
}
