package com.xpo.dfw.notification.mapper;

import com.xpo.dfw.notification.dto.NotificationRequest;
import com.xpo.dfw.notification.dto.NotificationResponse;
import com.xpo.dfw.notification.entity.Notification;
import com.xpo.dfw.notification.entity.NotificationStatus;
import org.springframework.stereotype.Component;

/**
 * Converts between {@link Notification} entities and their DTO
 * representations. Kept as a small, explicit mapper (rather than pulling
 * in MapStruct) to keep the codebase approachable.
 */
@Component
public class NotificationMapper {

    public Notification toEntity(NotificationRequest request) {
        return Notification.builder()
                .recipientId(request.getRecipientId())
                .recipientContact(request.getRecipientContact())
                .type(request.getType())
                .subject(request.getSubject())
                .message(request.getMessage())
                .referenceId(request.getReferenceId())
                .status(NotificationStatus.PENDING)
                .build();
    }

    public NotificationResponse toResponse(Notification entity) {
        return NotificationResponse.builder()
                .id(entity.getId())
                .recipientId(entity.getRecipientId())
                .recipientContact(entity.getRecipientContact())
                .type(entity.getType())
                .subject(entity.getSubject())
                .message(entity.getMessage())
                .status(entity.getStatus())
                .referenceId(entity.getReferenceId())
                .createdAt(entity.getCreatedAt())
                .sentAt(entity.getSentAt())
                .build();
    }
}
