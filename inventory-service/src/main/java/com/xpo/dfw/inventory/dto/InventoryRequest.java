package com.xpo.dfw.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

/**
 * Payload used to create or update an {@link com.xpo.dfw.inventory.entity.InventoryItem}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryRequest {

    @NotBlank(message = "sku is required")
    private String sku;

    @NotBlank(message = "productName is required")
    private String productName;

    private String description;

    @NotNull(message = "quantityAvailable is required")
    @PositiveOrZero(message = "quantityAvailable must be >= 0")
    private Integer quantityAvailable;

    @NotNull(message = "reorderLevel is required")
    @PositiveOrZero(message = "reorderLevel must be >= 0")
    private Integer reorderLevel;

    @NotNull(message = "unitPrice is required")
    @PositiveOrZero(message = "unitPrice must be >= 0")
    private BigDecimal unitPrice;

    private String warehouseLocation;
}
