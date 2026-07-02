package com.xpo.dfw.order.mapper;

import com.xpo.dfw.order.dto.OrderItemResponse;
import com.xpo.dfw.order.dto.OrderResponse;
import com.xpo.dfw.order.entity.Order;
import com.xpo.dfw.order.entity.OrderItem;
import org.springframework.stereotype.Component;

/**
 * Converts between {@link Order}/{@link OrderItem} entities and their DTO
 * representations. Kept as a small, explicit mapper (rather than pulling
 * in MapStruct) to keep the codebase approachable.
 */
@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .items(order.getItems().stream().map(this::toItemResponse).toList())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .sku(item.getSku())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                .build();
    }
}
