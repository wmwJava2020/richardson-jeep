package com.xpo.dfw.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xpo.dfw.notification.entity.NotificationStatus;
import com.xpo.dfw.notification.entity.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Representation of a {@link com.xpo.dfw.notification.entity.Notification}
 * returned to API consumers.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;
    private Long recipientId;
    private String recipientContact;
    private NotificationType type;
    private String subject;
    private String message;
    private NotificationStatus status;
    private String referenceId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime sentAt;
}
