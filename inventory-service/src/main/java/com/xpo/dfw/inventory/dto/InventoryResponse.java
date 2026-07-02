package com.xpo.dfw.inventory.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representation of an {@link com.xpo.dfw.inventory.entity.InventoryItem}
 * returned to API consumers.
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
    private String description;
    private Integer quantityAvailable;
    private Integer reorderLevel;
    private BigDecimal unitPrice;
    private String warehouseLocation;
    private boolean lowStock;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
