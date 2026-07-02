package com.xpo.dfw.order.dto.client;

import lombok.*;

/**
 * Minimal view of Notification Service's {@code NotificationResponse}
 * consumed by Order Service.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;
    private String status;
}
