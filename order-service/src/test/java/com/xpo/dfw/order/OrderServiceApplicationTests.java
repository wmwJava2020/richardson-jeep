package com.xpo.dfw.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Basic smoke test verifying the Spring application context loads
 * successfully with an in-memory H2 database and Eureka/Config-server
 * integrations disabled (see {@code src/test/resources/application.yml}).
 * Feign client beans are created as lazy proxies and do not require a
 * reachable discovery server or downstream service at context-load time.
 */
@SpringBootTest
class OrderServiceApplicationTests {

    @Test
    void contextLoads() {
        // If the application context fails to start, this test will fail.
    }
}
