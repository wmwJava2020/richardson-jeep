package com.xpo.dfw.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Customer Service - owns the {@code customer_db} database and exposes
 * REST APIs for managing customer profiles. Order Service validates
 * customer existence through this service's HTTP API before placing an
 * order.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
}
