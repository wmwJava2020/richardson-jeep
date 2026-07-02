package com.xpo.dfw.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Order Service - owns the {@code order_db} database and orchestrates the
 * order placement workflow: validating the customer (Customer Service),
 * checking and reserving stock (Inventory Service), and notifying the
 * customer of the outcome (Notification Service). Downstream calls are
 * made via declarative Feign clients wrapped with Resilience4j circuit
 * breakers and retries.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
