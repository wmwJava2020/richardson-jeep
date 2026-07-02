package com.xpo.dfw.inventory.dto;

import lombok.*;

/**
 * Result of a stock reservation / availability check, returned to the
 * calling service (typically Order Service).
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
