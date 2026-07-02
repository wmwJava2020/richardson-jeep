package com.xpo.dfw.order.dto.client;

import lombok.*;

/**
 * Subset of Customer Service's {@code CustomerResponse} consumed by Order
 * Service via {@link com.xpo.dfw.order.client.CustomerClient}. The
 * {@code status} field is kept as a plain {@code String} (rather than
 * sharing Customer Service's enum) to avoid coupling between services.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String status;
}
