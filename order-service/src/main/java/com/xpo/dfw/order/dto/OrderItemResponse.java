package com.xpo.dfw.order.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * Representation of a {@link com.xpo.dfw.order.entity.OrderItem} returned
 * to API consumers.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {

    private String sku;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
