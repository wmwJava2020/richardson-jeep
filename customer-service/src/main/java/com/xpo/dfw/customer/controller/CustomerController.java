package com.xpo.dfw.customer.controller;

import com.xpo.dfw.customer.dto.CustomerRequest;
import com.xpo.dfw.customer.dto.CustomerResponse;
import com.xpo.dfw.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for managing customers.
 * <p>
 * Base path: {@code /api/v1/customers}
 */
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer profile management")
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "Create a new customer")
    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(request));
    }
 /*
    @Operation(summary = "List all customers")
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }*/

    @Operation(summary = "Get a customer by their email")
    @GetMapping("/{email}")
    public ResponseEntity<CustomerResponse> getCustomerByEmail(@PathVariable("email") String email) {
        return ResponseEntity.ok(customerService.getCustomerByEmail(email));
    }
  /*
    @Operation(summary = "Update an existing customer")
    @PutMapping("/{email}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable("email") String email,
                                                             @Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(email, request));
    }

    @Operation(summary = "Delete a customer")
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable("email") String email) {
        customerService.deleteCustomer(Long.valueOf(email));
        return ResponseEntity.noContent().build();
    }*/
}
