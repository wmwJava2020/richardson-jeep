package com.xpo.dfw.order.client;

import com.xpo.dfw.order.dto.client.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Declarative HTTP client for Customer Service, resolved via Eureka
 * service discovery (load-balanced by Spring Cloud LoadBalancer).
 * Calls are wrapped with Resilience4j circuit breaker/retry in
 * {@code OrderServiceImpl} under the {@code customerService} instance
 * name.
 */
@FeignClient(name = "customer-service")
public interface CustomerClient {

    @GetMapping("/api/v1/customers/{id}")
    CustomerResponse getCustomerById(@PathVariable("id") Long id);
}
