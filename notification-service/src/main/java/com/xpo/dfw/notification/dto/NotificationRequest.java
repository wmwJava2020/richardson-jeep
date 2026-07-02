package com.xpo.dfw.notification.dto;

import com.xpo.dfw.notification.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Payload used to request dispatch of a new {@link com.xpo.dfw.notification.entity.Notification}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {

    @NotNull(message = "recipientId is required")
    private Long recipientId;

    @NotBlank(message = "recipientContact is required")
    private String recipientContact;

    @NotNull(message = "type is required")
    private NotificationType type;

    private String subject;

    @NotBlank(message = "message is required")
    private String message;

    /**
     * Optional correlation id, e.g. an order number, used to trace this
     * notification back to the event that triggered it.
     */
    private String referenceId;
}
