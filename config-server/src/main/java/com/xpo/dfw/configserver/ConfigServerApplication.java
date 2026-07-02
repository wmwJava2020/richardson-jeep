package com.xpo.dfw.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Centralized Configuration Management Server.
 * <p>
 * Serves environment-specific configuration (application.yml fragments) for
 * every Richardson-Jeep microservice from the bundled "native" config
 * repository located on the classpath at {@code /config}. Each service
 * imports its configuration on startup via
 * {@code spring.config.import=optional:configserver:http://config-server:8888}.
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
