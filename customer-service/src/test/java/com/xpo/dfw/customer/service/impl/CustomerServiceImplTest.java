package com.xpo.dfw.customer.service.impl;

import com.xpo.dfw.customer.dto.CustomerRequest;
import com.xpo.dfw.customer.dto.CustomerResponse;
import com.xpo.dfw.customer.entity.Customer;
import com.xpo.dfw.customer.mapper.CustomerMapper;
import com.xpo.dfw.customer.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void testGetCustomerByEmail() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setEmail("john@example.com");

        CustomerResponse mapped = new CustomerResponse(
                1L, "John", null, "john@example.com", null,
                null, null, null, null, null, null, null
        );

        when(customerRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(customer));

        when(customerMapper.toResponse(customer))
                .thenReturn(mapped);

        CustomerResponse response = customerService.getCustomerByEmail("john@example.com");

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
    }

    @Test
    void testCreateCustomer() {
        CustomerRequest request = new CustomerRequest();
        request.setFirstName("Alice");
        request.setEmail("alice@example.com");

        Customer entity = new Customer();
        entity.setFirstName("Alice");
        entity.setEmail("alice@example.com");

        Customer saved = new Customer();
        saved.setId(10L);
        saved.setFirstName("Alice");
        saved.setEmail("alice@example.com");

        CustomerResponse mapped = new CustomerResponse(
                10L, "Alice", null, "alice@example.com", null,
                null, null, null, null, null, null, null
        );

        when(customerRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(customerMapper.toEntity(request)).thenReturn(entity);
        when(customerRepository.save(entity)).thenReturn(saved);
        when(customerMapper.toResponse(saved)).thenReturn(mapped);

        CustomerResponse response = customerService.createCustomer(request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Alice", response.getFirstName());
    }
}
