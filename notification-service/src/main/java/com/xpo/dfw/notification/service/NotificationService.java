package com.xpo.dfw.notification.service;

import com.xpo.dfw.notification.dto.NotificationRequest;
import com.xpo.dfw.notification.dto.NotificationResponse;

import java.util.List;

/**
 * Business operations exposed by the Notification Service.
 */
public interface NotificationService {

    /**
     * Persists a new notification and attempts (simulated) delivery via
     * the appropriate channel, governed by the
     * {@code app.notification.channels} configuration.
     */
    NotificationResponse sendNotification(NotificationRequest request);

    NotificationResponse getNotificationById(Long id);

    List<NotificationResponse> getAllNotifications();

    List<NotificationResponse> getNotificationsByRecipient(Long recipientId);
}
