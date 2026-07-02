package com.xpo.dfw.customer.service;

import com.xpo.dfw.customer.dto.CustomerRequest;
import com.xpo.dfw.customer.dto.CustomerResponse;

import java.util.List;

/**
 * Business operations exposed by the Customer Service.
 */
public interface CustomerService {

    CustomerResponse createCustomer(CustomerRequest request);

    CustomerResponse getCustomerByEmail(String email);

    //List<CustomerResponse> getAllCustomers();

    //CustomerResponse updateCustomer(Long id, CustomerRequest request);

    //void deleteCustomer(Long id);
}
