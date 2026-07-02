package com.xpo.dfw.order.dto.client;

import lombok.*;

import java.math.BigDecimal;

/**
 * Subset of Inventory Service's {@code InventoryResponse} consumed by
 * Order Service via {@link com.xpo.dfw.order.client.InventoryClient}.
 * Unknown JSON properties returned by Inventory Service are ignored by
 * Jackson's default configuration.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponse {

    private Long id;
    private String sku;
    private String productName;
    private Integer quantityAvailable;
    private BigDecimal unitPrice;
    private boolean lowStock;
}
