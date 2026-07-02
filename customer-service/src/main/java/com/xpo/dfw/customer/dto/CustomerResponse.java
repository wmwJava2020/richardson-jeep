package com.xpo.dfw.customer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xpo.dfw.customer.entity.CustomerStatus;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Representation of a {@link com.xpo.dfw.customer.entity.Customer} returned
 * to API consumers, including Order Service when validating an order's
 * customer reference.
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
    private String addressLine;
    private String city;
    private String state;
    private String postalCode;
    private CustomerStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
