package com.xpo.dfw.order.dto.client;

import lombok.*;

/**
 * Mirrors Notification Service's {@code NotificationRequest}, sent by
 * {@link com.xpo.dfw.order.client.NotificationClient} to notify a
 * customer about an order's outcome. {@code type} is kept as a plain
 * {@code String} ({@code "EMAIL"} or {@code "SMS"}) to avoid coupling
 * to Notification Service's enum.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {

    private Long recipientId;
    private String recipientContact;
    private String type;
    private String subject;
    private String message;
    private String referenceId;
}
