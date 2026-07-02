package com.xpo.dfw.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway - single entry point for all client traffic into the
 * Richardson-Jeep platform.
 * <p>
 * Responsibilities:
 * <ul>
 *     <li>Routes incoming HTTP requests to the appropriate downstream
 *         microservice (inventory, order, notification, customer) using
 *         load-balanced ({@code lb://}) URIs resolved via Eureka.</li>
 *     <li>Applies cross-cutting concerns: CORS, circuit breakers,
 *         request/response logging headers and centralized routing
 *         configuration.</li>
 * </ul>
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
