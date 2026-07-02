package com.xpo.dfw.inventory.service.impl;

import com.xpo.dfw.inventory.dto.InventoryRequest;
import com.xpo.dfw.inventory.dto.InventoryResponse;
import com.xpo.dfw.inventory.dto.StockReservationRequest;
import com.xpo.dfw.inventory.dto.StockReservationResponse;
import com.xpo.dfw.inventory.entity.InventoryItem;
import com.xpo.dfw.inventory.exception.DuplicateResourceException;
import com.xpo.dfw.inventory.exception.ResourceNotFoundException;
import com.xpo.dfw.inventory.mapper.InventoryMapper;
import com.xpo.dfw.inventory.repository.InventoryRepository;
import com.xpo.dfw.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default implementation of {@link InventoryService} backed by
 * {@link InventoryRepository} (MySQL via Spring Data JPA).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    @Transactional
    public InventoryResponse createItem(InventoryRequest request) {
        if (inventoryRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("Inventory item with sku '" + request.getSku() + "' already exists");
        }
        InventoryItem entity = inventoryMapper.toEntity(request);
        InventoryItem saved = inventoryRepository.save(entity);
        log.info("Created inventory item id={} sku={}", saved.getId(), saved.getSku());
        return inventoryMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getItemById(Long id) {
        return inventoryMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getItemBySku(String sku) {
        InventoryItem item = inventoryRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item with sku '" + sku + "' not found"));
        return inventoryMapper.toResponse(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllItems() {
        return inventoryRepository.findAll().stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public InventoryResponse updateItem(Long id, InventoryRequest request) {
        InventoryItem existing = findEntityById(id);

        // If the SKU is being changed, make sure the new value isn't already taken.
        if (!existing.getSku().equalsIgnoreCase(request.getSku())
                && inventoryRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("Inventory item with sku '" + request.getSku() + "' already exists");
        }

        inventoryMapper.updateEntity(existing, request);
        InventoryItem saved = inventoryRepository.save(existing);
        log.info("Updated inventory item id={} sku={}", saved.getId(), saved.getSku());
        return inventoryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteItem(Long id) {
        InventoryItem existing = findEntityById(id);
        inventoryRepository.delete(existing);
        log.info("Deleted inventory item id={} sku={}", id, existing.getSku());
    }

    @Override
    @Transactional(readOnly = true)
    public StockReservationResponse checkAvailability(String sku, Integer quantity) {
        InventoryItem item = inventoryRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item with sku '" + sku + "' not found"));

        boolean available = item.getQuantityAvailable() >= quantity;
        return StockReservationResponse.builder()
                .sku(sku)
                .reserved(available)
                .remainingQuantity(item.getQuantityAvailable())
                .message(available ? "Sufficient stock available" : "Insufficient stock available")
                .build();
    }

    @Override
    @Transactional
    public StockReservationResponse reserveStock(StockReservationRequest request) {
        InventoryItem item = inventoryRepository.findBySku(request.getSku())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item with sku '" + request.getSku() + "' not found"));

        if (item.getQuantityAvailable() < request.getQuantity()) {
            log.warn("Insufficient stock for sku={} requested={} available={} reference={}",
                    request.getSku(), request.getQuantity(), item.getQuantityAvailable(), request.getReferenceId());
            return StockReservationResponse.builder()
                    .sku(request.getSku())
                    .reserved(false)
                    .remainingQuantity(item.getQuantityAvailable())
                    .message("Insufficient stock available")
                    .build();
        }

        item.setQuantityAvailable(item.getQuantityAvailable() - request.getQuantity());
        inventoryRepository.save(item);

        if (item.isLowStock()) {
            log.warn("Inventory low-stock alert: sku={} remaining={} reorderLevel={}",
                    item.getSku(), item.getQuantityAvailable(), item.getReorderLevel());
        }

        log.info("Reserved {} unit(s) of sku={} for reference={}. Remaining={}",
                request.getQuantity(), request.getSku(), request.getReferenceId(), item.getQuantityAvailable());

        return StockReservationResponse.builder()
                .sku(request.getSku())
                .reserved(true)
                .remainingQuantity(item.getQuantityAvailable())
                .message("Stock reserved successfully")
                .build();
    }

    private InventoryItem findEntityById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item with id '" + id + "' not found"));
    }
}
