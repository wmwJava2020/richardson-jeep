package com.xpo.dfw.order.client;

import com.xpo.dfw.order.dto.client.InventoryResponse;
import com.xpo.dfw.order.dto.client.StockReservationRequest;
import com.xpo.dfw.order.dto.client.StockReservationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Declarative HTTP client for Inventory Service, resolved via Eureka
 * service discovery (load-balanced by Spring Cloud LoadBalancer).
 * Calls are wrapped with Resilience4j circuit breaker/retry in
 * {@code OrderServiceImpl} under the {@code inventoryService} instance
 * name (matching the resilience4j configuration in
 * {@code application.yml} / Config Server's {@code order-service.yml}).
 */
@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @GetMapping("/api/v1/inventory/sku/{sku}")
    InventoryResponse getBySku(@PathVariable("sku") String sku);

    @GetMapping("/api/v1/inventory/availability/{sku}")
    StockReservationResponse checkAvailability(@PathVariable("sku") String sku,
                                                 @RequestParam("quantity") Integer quantity);

    @PostMapping("/api/v1/inventory/reserve")
    StockReservationResponse reserveStock(@RequestBody StockReservationRequest request);
}
