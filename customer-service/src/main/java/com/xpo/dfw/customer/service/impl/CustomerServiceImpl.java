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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default implementation of {@link CustomerService} backed by
 * {@link CustomerRepository} (MySQL via Spring Data JPA).
 */
@Service
//@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;


    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) { //Check for duplicate email
            throw new DuplicateResourceException("Customer with email '" + request.getEmail() + "' already exists");
        }
        Customer entity = customerMapper.toEntity(request); //Convert request → entity
        Customer saved = customerRepository.save(entity); //Save entity
        log.info("Created customer id={} email={}", saved.getId(), saved.getEmail());
        return customerMapper.toResponse(saved);//Convert saved entity → response
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByEmail(String email) {
        log.info("Fetching customer by email={}", email);
        return customerMapper.toResponse(findEntityByEmail(email));
    }

    //@Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    private Customer findEntityByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with email '" + email + "' not found"));
    }
    @Transactional
    public CustomerResponse updateCustomer(String email, CustomerRequest request) {
        Customer existing = findEntityByEmail(email);
       log.info("Updating customer id={} email={}", existing.getId(), existing.getEmail());
        if (!existing.getEmail().equalsIgnoreCase(request.getEmail())
                && customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Customer with email '" + request.getEmail() + "' already exists");
        }

        customerMapper.updateEntity(existing, request);
        log.info("Updated customer entity id={} email={}", existing.getId(), existing.getEmail());

        Customer saved = customerRepository.save(existing);
        log.info("Updated customer id={} email={}", saved.getId(), saved.getEmail());
        return customerMapper.toResponse(saved);
    }

}
