package com.xpo.dfw.inventory.service;

import com.xpo.dfw.inventory.dto.InventoryRequest;
import com.xpo.dfw.inventory.dto.InventoryResponse;
import com.xpo.dfw.inventory.dto.StockReservationRequest;
import com.xpo.dfw.inventory.dto.StockReservationResponse;

import java.util.List;

/**
 * Business operations exposed by the Inventory Service.
 */
public interface InventoryService {

    InventoryResponse createItem(InventoryRequest request);

    InventoryResponse getItemById(Long id);

    InventoryResponse getItemBySku(String sku);

    List<InventoryResponse> getAllItems();

    InventoryResponse updateItem(Long id, InventoryRequest request);

    void deleteItem(Long id);

    /**
     * Checks whether the requested quantity of a SKU is available without
     * mutating stock levels.
     */
    StockReservationResponse checkAvailability(String sku, Integer quantity);

    /**
     * Atomically decrements available stock for the given SKU, used when
     * Order Service confirms an order.
     */
    StockReservationResponse reserveStock(StockReservationRequest request);
}
