package com.xpo.dfw.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

/**
 * Generic fallback endpoints invoked by the Resilience4j circuit breaker
 * gateway filter (see {@code application.yml} routes) whenever a downstream
 * Richardson-Jeep microservice is unavailable or its circuit breaker is open.
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/inventory")
    public Mono<ResponseEntity<Map<String, Object>>> inventoryFallback() {
        return buildFallbackResponse("inventory-service");
    }

    @RequestMapping("/orders")
    public Mono<ResponseEntity<Map<String, Object>>> ordersFallback() {
        return buildFallbackResponse("order-service");
    }

    @RequestMapping("/notifications")
    public Mono<ResponseEntity<Map<String, Object>>> notificationsFallback() {
        return buildFallbackResponse("notification-service");
    }

    @RequestMapping("/customers")
    public Mono<ResponseEntity<Map<String, Object>>> customersFallback() {
        return buildFallbackResponse("customer-service");
    }

    private Mono<ResponseEntity<Map<String, Object>>> buildFallbackResponse(String service) {
        Map<String, Object> body = Map.of(
                "timestamp", Instant.now().toString(),
                "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
                "error", "Service Unavailable",
                "service", service,
                "message", service + " is temporarily unavailable. Please try again shortly."
        );
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body));
    }
}
