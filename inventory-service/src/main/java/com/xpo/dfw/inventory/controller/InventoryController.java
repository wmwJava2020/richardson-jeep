package com.xpo.dfw.inventory.controller;

import com.xpo.dfw.inventory.dto.InventoryRequest;
import com.xpo.dfw.inventory.dto.InventoryResponse;
import com.xpo.dfw.inventory.dto.StockReservationRequest;
import com.xpo.dfw.inventory.dto.StockReservationResponse;
import com.xpo.dfw.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for managing warehouse inventory.
 * <p>
 * Base path: {@code /api/v1/inventory}
 */
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Warehouse inventory management")
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "Create a new inventory item")
    @PostMapping
    public ResponseEntity<InventoryResponse> createItem(@Valid @RequestBody InventoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.createItem(request));
    }

    @Operation(summary = "List all inventory items")
    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAllItems() {
        return ResponseEntity.ok(inventoryService.getAllItems());
    }

    @Operation(summary = "Get an inventory item by its database id")
    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> getItemById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(inventoryService.getItemById(id));
    }

    @Operation(summary = "Get an inventory item by SKU")
    @GetMapping("/sku/{sku}")
    public ResponseEntity<InventoryResponse> getItemBySku(@PathVariable("sku") String sku) {
        return ResponseEntity.ok(inventoryService.getItemBySku(sku));
    }

    @Operation(summary = "Update an existing inventory item")
    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponse> updateItem(@PathVariable("id") Long id,
                                                          @Valid @RequestBody InventoryRequest request) {
        return ResponseEntity.ok(inventoryService.updateItem(id, request));
    }

    @Operation(summary = "Delete an inventory item")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable("id") Long id) {
        inventoryService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Check stock availability for a SKU without reserving it")
    @GetMapping("/availability/{sku}")
    public ResponseEntity<StockReservationResponse> checkAvailability(
            @PathVariable("sku") String sku,
            @RequestParam("quantity") @Min(1) Integer quantity) {
        return ResponseEntity.ok(inventoryService.checkAvailability(sku, quantity));
    }

    @Operation(summary = "Reserve (decrement) stock for a SKU - called by Order Service")
    @PostMapping("/reserve")
    public ResponseEntity<StockReservationResponse> reserveStock(@Valid @RequestBody StockReservationRequest request) {
        return ResponseEntity.ok(inventoryService.reserveStock(request));
    }
}
