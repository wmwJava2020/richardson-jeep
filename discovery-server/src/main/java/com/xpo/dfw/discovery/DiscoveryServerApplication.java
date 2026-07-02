package com.xpo.dfw.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Service Discovery Server (Netflix Eureka) for the Richardson-Jeep platform.
 * <p>
 * All microservices (inventory, order, notification, customer) and the
 * API Gateway register themselves with this server so they can discover
 * and call each other by logical service name instead of hardcoded
 * host/port combinations.
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServerApplication.class, args);
    }
}
