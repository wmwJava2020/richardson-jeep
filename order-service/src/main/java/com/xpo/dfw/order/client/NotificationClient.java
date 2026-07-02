package com.xpo.dfw.order.client;

import com.xpo.dfw.order.dto.client.NotificationRequest;
import com.xpo.dfw.order.dto.client.NotificationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Declarative HTTP client for Notification Service, resolved via Eureka
 * service discovery (load-balanced by Spring Cloud LoadBalancer).
 * Calls are wrapped with Resilience4j circuit breaker/retry in
 * {@code OrderServiceImpl} under the {@code notificationService}
 * instance name. A failure here is logged and swallowed by the fallback
 * - notification delivery must never fail order creation.
 */
@FeignClient(name = "notification-service")
public interface NotificationClient {

    @PostMapping("/api/v1/notifications")
    NotificationResponse sendNotification(@RequestBody NotificationRequest request);
}
