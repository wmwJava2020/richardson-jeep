package com.xpo.dfw.notification;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Basic smoke test verifying the Spring application context loads
 * successfully with an in-memory H2 database and Eureka/Config-server
 * integrations disabled (see {@code src/test/resources/application.yml}).
 */
@SpringBootTest
class NotificationServiceApplicationTests {

    @Test
    void contextLoads() {
        // If the application context fails to start, this test will fail.
    }
}
