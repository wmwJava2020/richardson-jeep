package com.xpo.dfw.notification.service.impl;

import com.xpo.dfw.notification.dto.NotificationRequest;
import com.xpo.dfw.notification.dto.NotificationResponse;
import com.xpo.dfw.notification.entity.Notification;
import com.xpo.dfw.notification.entity.NotificationStatus;
import com.xpo.dfw.notification.entity.NotificationType;
import com.xpo.dfw.notification.exception.ResourceNotFoundException;
import com.xpo.dfw.notification.mapper.NotificationMapper;
import com.xpo.dfw.notification.repository.NotificationRepository;
import com.xpo.dfw.notification.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Default implementation of {@link NotificationService}. Delivery to a
 * real email/SMS provider is simulated: the notification is persisted
 * with status {@code PENDING}, then immediately marked {@code SENT}
 * (or {@code FAILED} if its channel is disabled via configuration),
 * optionally pausing for a configurable delay to emulate provider
 * latency.
 */
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final boolean emailEnabled;
    private final boolean smsEnabled;
    private final long simulatedDeliveryDelayMs;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                    NotificationMapper notificationMapper,
                                    @Value("${app.notification.channels.email-enabled:true}") boolean emailEnabled,
                                    @Value("${app.notification.channels.sms-enabled:true}") boolean smsEnabled,
                                    @Value("${app.notification.simulated-delivery-delay-ms:0}") long simulatedDeliveryDelayMs) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.emailEnabled = emailEnabled;
        this.smsEnabled = smsEnabled;
        this.simulatedDeliveryDelayMs = simulatedDeliveryDelayMs;
    }

    @Override
    @Transactional
    public NotificationResponse sendNotification(NotificationRequest request) {
        Notification entity = notificationMapper.toEntity(request);
        Notification saved = notificationRepository.save(entity);

        boolean channelEnabled = isChannelEnabled(saved.getType());
        simulateDeliveryDelay();

        if (channelEnabled) {
            saved.setStatus(NotificationStatus.SENT);
            saved.setSentAt(LocalDateTime.now());
            log.info("Notification id={} type={} to recipient={} marked SENT",
                    saved.getId(), saved.getType(), saved.getRecipientId());
        } else {
            saved.setStatus(NotificationStatus.FAILED);
            log.warn("Notification id={} type={} to recipient={} marked FAILED - channel disabled",
                    saved.getId(), saved.getType(), saved.getRecipientId());
        }

        Notification updated = notificationRepository.save(saved);
        return notificationMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(Long id) {
        return notificationMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByRecipient(Long recipientId) {
        return notificationRepository.findByRecipientId(recipientId).stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    private boolean isChannelEnabled(NotificationType type) {
        return switch (type) {
            case EMAIL -> emailEnabled;
            case SMS -> smsEnabled;
        };
    }

    private void simulateDeliveryDelay() {
        if (simulatedDeliveryDelayMs <= 0) {
            return;
        }
        try {
            Thread.sleep(simulatedDeliveryDelayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Notification findEntityById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification with id '" + id + "' not found"));
    }
}
