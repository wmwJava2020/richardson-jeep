package com.xpo.dfw.notification.controller;

import com.xpo.dfw.notification.dto.NotificationRequest;
import com.xpo.dfw.notification.dto.NotificationResponse;
import com.xpo.dfw.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for dispatching and querying notifications.
 * <p>
 * Base path: {@code /api/v1/notifications}
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Customer notification dispatch")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Send (simulate dispatch of) a new notification - called by Order Service")
    @PostMapping
    public ResponseEntity<NotificationResponse> sendNotification(@Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.sendNotification(request));
    }

    @Operation(summary = "List all notifications")
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @Operation(summary = "Get a notification by its database id")
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    @Operation(summary = "List notifications sent to a given recipient")
    @GetMapping("/recipient/{recipientId}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByRecipient(@PathVariable("recipientId") Long recipientId) {
        return ResponseEntity.ok(notificationService.getNotificationsByRecipient(recipientId));
    }
}
