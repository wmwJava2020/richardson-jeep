package com.xpo.dfw.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Inventory Service - owns the {@code inventory_db} database and exposes
 * REST APIs for managing warehouse stock (SKUs, quantities, reorder
 * levels). Other services (notably Order Service) check and reserve
 * stock through this service's HTTP API.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}
