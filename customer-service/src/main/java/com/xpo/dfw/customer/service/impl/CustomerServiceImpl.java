package com.xpo.dfw.customer.service.impl;

import com.xpo.dfw.customer.dto.CustomerRequest;
import com.xpo.dfw.customer.dto.CustomerResponse;
import com.xpo.dfw.customer.entity.Customer;
import com.xpo.dfw.customer.exception.DuplicateResourceException;
import com.xpo.dfw.customer.exception.ResourceNotFoundException;
import com.xpo.dfw.customer.mapper.CustomerMapper;
import com.xpo.dfw.customer.repository.CustomerRepository;
import com.xpo.dfw.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default implementation of {@link CustomerService} backed by
 * {@link CustomerRepository} (MySQL via Spring Data JPA).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Customer with email '" + request.getEmail() + "' already exists");
        }
        Customer entity = customerMapper.toEntity(request);
        Customer saved = customerRepository.save(entity);
        log.info("Created customer id={} email={}", saved.getId(), saved.getEmail());
        return customerMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        return customerMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer existing = findEntityById(id);

        if (!existing.getEmail().equalsIgnoreCase(request.getEmail())
                && customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Customer with email '" + request.getEmail() + "' already exists");
        }

        customerMapper.updateEntity(existing, request);
        Customer saved = customerRepository.save(existing);
        log.info("Updated customer id={} email={}", saved.getId(), saved.getEmail());
        return customerMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        Customer existing = findEntityById(id);
        customerRepository.delete(existing);
        log.info("Deleted customer id={} email={}", id, existing.getEmail());
    }

    private Customer findEntityById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id '" + id + "' not found"));
    }
}
