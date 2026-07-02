package com.xpo.dfw.order.service;

import com.xpo.dfw.order.dto.OrderRequest;
import com.xpo.dfw.order.dto.OrderResponse;
import com.xpo.dfw.order.entity.OrderStatus;

import java.util.List;

/**
 * Business operations exposed by the Order Service.
 */
public interface OrderService {

    /**
     * Places a new order: validates the customer, checks/reserves stock
     * for each line item, persists the order, and sends a best-effort
     * notification to the customer about the outcome.
     */
    OrderResponse createOrder(OrderRequest request);

    OrderResponse getOrderById(Long id);

    List<OrderResponse> getAllOrders();

    List<OrderResponse> getOrdersByCustomer(Long customerId);

    /**
     * Transitions an order to the given status, subject to the
     * permitted-transition rules in {@link com.xpo.dfw.order.entity.OrderStatus}.
     */
    OrderResponse updateOrderStatus(Long id, OrderStatus status);

    /**
     * Cancels an order (sets its status to {@code CANCELLED}), unless it
     * is already in a terminal state.
     */
    OrderResponse cancelOrder(Long id);
}
