package com.xpo.dfw.inventory.mapper;

import com.xpo.dfw.inventory.dto.InventoryRequest;
import com.xpo.dfw.inventory.dto.InventoryResponse;
import com.xpo.dfw.inventory.entity.InventoryItem;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * Converts between {@link InventoryItem} entities and their DTO
 * representations. Kept as a small, explicit mapper (rather than pulling
 * in MapStruct) to keep the codebase approachable.
 */
@Component
public class InventoryMapper {

    private static final Logger log = Logger.getLogger(String.valueOf(InventoryMapper.class));

    public InventoryItem toEntity(InventoryRequest request) {
        log.info("Mapping InventoryRequest to InventoryItem entity: " + request);
        return InventoryItem.builder()
                .sku(request.getSku())
                .productName(request.getProductName())
                .description(request.getDescription())
                .quantityAvailable(request.getQuantityAvailable())
                .reorderLevel(request.getReorderLevel())
                .unitPrice(request.getUnitPrice())
                .warehouseLocation(request.getWarehouseLocation())
                .build();
    }

    /**
     * Applies the values from an {@link InventoryRequest} onto an existing
     * managed entity (used for updates), preserving identity and audit
     * fields.
     */
    public void updateEntity(InventoryItem entity, InventoryRequest request) {
        log.info("Updating InventoryItem entity with InventoryRequest: " + request);
        entity.setSku(request.getSku());
        entity.setProductName(request.getProductName());
        entity.setDescription(request.getDescription());
        entity.setQuantityAvailable(request.getQuantityAvailable());
        entity.setReorderLevel(request.getReorderLevel());
        entity.setUnitPrice(request.getUnitPrice());
        entity.setWarehouseLocation(request.getWarehouseLocation());
    }

    public InventoryResponse toResponse(InventoryItem entity) {
        return InventoryResponse.builder()
                .id(entity.getId())
                .sku(entity.getSku())
                .productName(entity.getProductName())
                .description(entity.getDescription())
                .quantityAvailable(entity.getQuantityAvailable())
                .reorderLevel(entity.getReorderLevel())
                .unitPrice(entity.getUnitPrice())
                .warehouseLocation(entity.getWarehouseLocation())
                .lowStock(entity.isLowStock())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
