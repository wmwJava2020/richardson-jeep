package com.xpo.dfw.order.service.impl;

import com.xpo.dfw.order.client.CustomerClient;
import com.xpo.dfw.order.client.InventoryClient;
import com.xpo.dfw.order.client.NotificationClient;
import com.xpo.dfw.order.dto.OrderItemRequest;
import com.xpo.dfw.order.dto.OrderRequest;
import com.xpo.dfw.order.dto.OrderResponse;
import com.xpo.dfw.order.dto.client.CustomerResponse;
import com.xpo.dfw.order.dto.client.InventoryResponse;
import com.xpo.dfw.order.dto.client.NotificationRequest;
import com.xpo.dfw.order.dto.client.StockReservationRequest;
import com.xpo.dfw.order.dto.client.StockReservationResponse;
import com.xpo.dfw.order.entity.Order;
import com.xpo.dfw.order.entity.OrderItem;
import com.xpo.dfw.order.entity.OrderStatus;
import com.xpo.dfw.order.exception.CustomerServiceUnavailableException;
import com.xpo.dfw.order.exception.InvalidOrderStateException;
import com.xpo.dfw.order.exception.ResourceNotFoundException;
import com.xpo.dfw.order.mapper.OrderMapper;
import com.xpo.dfw.order.repository.OrderRepository;
import com.xpo.dfw.order.service.OrderService;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Default implementation of {@link OrderService}.
 * <p>
 * Order creation orchestrates three downstream services via Feign
 * clients, each protected by a Resilience4j circuit breaker and retry
 * (instance names {@code customerService}, {@code inventoryService}, and
 * {@code notificationService} - configured in {@code application.yml} and
 * centrally in Config Server's {@code order-service.yml}):
 * <ol>
 *     <li>Customer Service - confirms the placing customer exists</li>
 *     <li>Inventory Service - checks availability, resolves pricing, and
 *     reserves stock per line item</li>
 *     <li>Notification Service - sends a best-effort order outcome
 *     notification</li>
 * </ol>
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerClient customerClient;
    private final InventoryClient inventoryClient;
    private final NotificationClient notificationClient;
    private final OrderMapper orderMapper;
    private final boolean autoConfirm;
    private final String orderNumberPrefix;

    private static final Set<OrderStatus> TERMINAL_STATUSES =
            Set.of(OrderStatus.REJECTED, OrderStatus.CANCELLED);

    public OrderServiceImpl(OrderRepository orderRepository,
                             CustomerClient customerClient,
                             InventoryClient inventoryClient,
                             NotificationClient notificationClient,
                             OrderMapper orderMapper,
                             @Value("${app.order.auto-confirm:true}") boolean autoConfirm,
                             @Value("${app.order.order-number-prefix:RJ-ORD-}") String orderNumberPrefix) {
        this.orderRepository = orderRepository;
        this.customerClient = customerClient;
        this.inventoryClient = inventoryClient;
        this.notificationClient = notificationClient;
        this.orderMapper = orderMapper;
        this.autoConfirm = autoConfirm;
        this.orderNumberPrefix = orderNumberPrefix;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        log.info("Creating order for customerId={} with {} item(s)", request.getCustomerId(), request.getItems().size());

        // 1. Validate the customer before doing any inventory work.
        CustomerResponse customer = fetchCustomer(request.getCustomerId());

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .customerId(request.getCustomerId())
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        // 2. Check availability and resolve pricing for each requested item.
        boolean allAvailable = true;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {
            StockReservationResponse availability = checkAvailability(itemRequest.getSku(), itemRequest.getQuantity());

            if (!availability.isReserved()) {
                allAvailable = false;
                log.warn("Item sku={} unavailable for order: {}", itemRequest.getSku(), availability.getMessage());
                order.addItem(OrderItem.builder()
                        .sku(itemRequest.getSku())
                        .productName("UNAVAILABLE")
                        .quantity(itemRequest.getQuantity())
                        .unitPrice(BigDecimal.ZERO)
                        .build());
                continue;
            }

            InventoryResponse inventoryItem = fetchInventoryItem(itemRequest.getSku());
            if (inventoryItem == null) {
                allAvailable = false;
                order.addItem(OrderItem.builder()
                        .sku(itemRequest.getSku())
                        .productName("UNAVAILABLE")
                        .quantity(itemRequest.getQuantity())
                        .unitPrice(BigDecimal.ZERO)
                        .build());
                continue;
            }

            order.addItem(OrderItem.builder()
                    .sku(itemRequest.getSku())
                    .productName(inventoryItem.getProductName())
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(inventoryItem.getUnitPrice())
                    .build());
            totalAmount = totalAmount.add(inventoryItem.getUnitPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
        }

        // 3. Reserve stock for all items only if every item was available.
        if (allAvailable) {
            for (OrderItemRequest itemRequest : request.getItems()) {
                StockReservationResponse reservation = reserveStock(itemRequest.getSku(), itemRequest.getQuantity(), order.getOrderNumber());
                if (!reservation.isReserved()) {
                    allAvailable = false;
                    log.warn("Reservation failed for sku={} on order {}: {}",
                            itemRequest.getSku(), order.getOrderNumber(), reservation.getMessage());
                    break;
                }
            }
        }

        if (allAvailable) {
            order.setStatus(autoConfirm ? OrderStatus.CONFIRMED : OrderStatus.PENDING);
            order.setTotalAmount(totalAmount);
        } else {
            order.setStatus(OrderStatus.REJECTED);
            order.setTotalAmount(BigDecimal.ZERO);
        }

        Order saved = orderRepository.save(order);
        log.info("Order {} saved with status={} totalAmount={}", saved.getOrderNumber(), saved.getStatus(), saved.getTotalAmount());

        // 4. Best-effort notification of the outcome.
        notifyCustomer(customer, saved);

        return orderMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        return orderMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
        Order order = findEntityById(id);
        if (TERMINAL_STATUSES.contains(order.getStatus())) {
            throw new InvalidOrderStateException(
                    "Order " + order.getOrderNumber() + " is in terminal state " + order.getStatus() + " and cannot be updated");
        }
        order.setStatus(status);
        Order saved = orderRepository.save(order);
        log.info("Order {} status updated to {}", saved.getOrderNumber(), saved.getStatus());
        return orderMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long id) {
        Order order = findEntityById(id);
        if (TERMINAL_STATUSES.contains(order.getStatus())) {
            throw new InvalidOrderStateException(
                    "Order " + order.getOrderNumber() + " is already " + order.getStatus() + " and cannot be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);
        log.info("Order {} cancelled", saved.getOrderNumber());
        return orderMapper.toResponse(saved);
    }

    // ------------------------------------------------------------------
    // Downstream service calls (Resilience4j circuit breaker + retry)
    // ------------------------------------------------------------------

    @CircuitBreaker(name = "customerService", fallbackMethod = "fetchCustomerFallback")
    @Retry(name = "customerService")
    public CustomerResponse fetchCustomer(Long customerId) {
        try {
            return customerClient.getCustomerById(customerId);
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("Customer with id '" + customerId + "' not found");
        } catch (FeignException ex) {
            log.error("Customer Service unavailable while validating customerId={}: {}", customerId, ex.toString());
            throw new CustomerServiceUnavailableException("Customer Service is currently unavailable. Please try again later.");
        }
    }

    @SuppressWarnings("unused")
    private CustomerResponse fetchCustomerFallback(Long customerId, Throwable throwable) {
        if (throwable instanceof FeignException.NotFound) {
            throw new ResourceNotFoundException("Customer with id '" + customerId + "' not found");
        }
        log.error("Customer Service unavailable while validating customerId={}: {}", customerId, throwable.toString());
        throw new CustomerServiceUnavailableException("Customer Service is currently unavailable. Please try again later.");
    }

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "checkAvailabilityFallback")
    @Retry(name = "inventoryService")
    public StockReservationResponse checkAvailability(String sku, Integer quantity) {
        try {
            return inventoryClient.checkAvailability(sku, quantity);
        } catch (FeignException.NotFound ex) {
            log.warn("Inventory item not found while checking availability for sku={}", sku);
            return unavailableInventoryResponse(sku, "Inventory item with sku " + sku + " was not found");
        } catch (FeignException ex) {
            return checkAvailabilityFallback(sku, quantity, ex);
        }
    }

    @SuppressWarnings("unused")
    private StockReservationResponse checkAvailabilityFallback(String sku, Integer quantity, Throwable throwable) {
        log.error("Inventory Service unavailable while checking availability for sku={}: {}", sku, throwable.toString());
        return StockReservationResponse.builder()
                .sku(sku)
                .reserved(false)
                .remainingQuantity(0)
                .message("Inventory Service unavailable - unable to confirm availability for sku " + sku)
                .build();
    }

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "fetchInventoryItemFallback")
    @Retry(name = "inventoryService")
    public InventoryResponse fetchInventoryItem(String sku) {
        try {
            return inventoryClient.getBySku(sku);
        } catch (FeignException.NotFound ex) {
            log.warn("Inventory item not found while fetching item details for sku={}", sku);
            return null;
        } catch (FeignException ex) {
            return fetchInventoryItemFallback(sku, ex);
        }
    }

    @SuppressWarnings("unused")
    private InventoryResponse fetchInventoryItemFallback(String sku, Throwable throwable) {
        log.error("Inventory Service unavailable while fetching item details for sku={}: {}", sku, throwable.toString());
        return null;
    }

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "reserveStockFallback")
    @Retry(name = "inventoryService")
    public StockReservationResponse reserveStock(String sku, Integer quantity, String referenceId) {
        try {
            return inventoryClient.reserveStock(StockReservationRequest.builder()
                    .sku(sku)
                    .quantity(quantity)
                    .referenceId(referenceId)
                    .build());
        } catch (FeignException.NotFound ex) {
            log.warn("Inventory item not found while reserving sku={} for order {}", sku, referenceId);
            return unavailableInventoryResponse(sku, "Inventory item with sku " + sku + " was not found");
        } catch (FeignException ex) {
            return reserveStockFallback(sku, quantity, referenceId, ex);
        }
    }

    @SuppressWarnings("unused")
    private StockReservationResponse reserveStockFallback(String sku, Integer quantity, String referenceId, Throwable throwable) {
        log.error("Inventory Service unavailable while reserving sku={} for order {}: {}", sku, referenceId, throwable.toString());
        return StockReservationResponse.builder()
                .sku(sku)
                .reserved(false)
                .remainingQuantity(0)
                .message("Inventory Service unavailable - unable to reserve sku " + sku)
                .build();
    }

    @CircuitBreaker(name = "notificationService", fallbackMethod = "notifyCustomerFallback")
    @Retry(name = "notificationService")
    public void notifyCustomer(CustomerResponse customer, Order order) {
        String subject = "Order " + order.getOrderNumber() + " - " + order.getStatus();
        String message = switch (order.getStatus()) {
            case CONFIRMED -> "Your order " + order.getOrderNumber() + " has been confirmed. Total: " + order.getTotalAmount();
            case PENDING -> "Your order " + order.getOrderNumber() + " has been received and is pending confirmation.";
            case REJECTED -> "Your order " + order.getOrderNumber() + " could not be fulfilled due to stock availability.";
            default -> "Your order " + order.getOrderNumber() + " status is now " + order.getStatus() + ".";
        };

        try {
            notificationClient.sendNotification(NotificationRequest.builder()
                    .recipientId(customer.getId())
                    .recipientContact(customer.getEmail())
                    .type("EMAIL")
                    .subject(subject)
                    .message(message)
                    .referenceId(order.getOrderNumber())
                    .build());
        } catch (RuntimeException ex) {
            notifyCustomerFallback(customer, order, ex);
        }
    }

    @SuppressWarnings("unused")
    private void notifyCustomerFallback(CustomerResponse customer, Order order, Throwable throwable) {
        log.warn("Notification Service unavailable; order outcome notification for order {} not sent: {}",
                order.getOrderNumber(), throwable.toString());
        // Intentionally swallowed: a notification failure must not fail order creation.
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private String generateOrderNumber() {
        return orderNumberPrefix + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private StockReservationResponse unavailableInventoryResponse(String sku, String message) {
        return StockReservationResponse.builder()
                .sku(sku)
                .reserved(false)
                .remainingQuantity(0)
                .message(message)
                .build();
    }

    private Order findEntityById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id '" + id + "' not found"));
    }
}
