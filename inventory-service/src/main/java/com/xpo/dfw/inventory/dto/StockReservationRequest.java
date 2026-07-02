package com.xpo.dfw.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

/**
 * Request sent (typically by Order Service) to reserve / decrement stock
 * for a given SKU when an order is placed.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockReservationRequest {

    @NotBlank(message = "sku is required")
    private String sku;

    @NotNull(message = "quantity is required")
    @Positive(message = "quantity must be > 0")
    private Integer quantity;

    /**
     * Optional correlation id (e.g. order number) used purely for logging
     * / traceability across services.
     */
    private String referenceId;
}
