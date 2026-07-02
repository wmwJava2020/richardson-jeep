package com.xpo.dfw.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Notification Service - owns the {@code notification_db} database and
 * exposes a REST API used by other services (notably Order Service) to
 * dispatch customer notifications (email/SMS). Delivery to real
 * providers is simulated for this project.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
