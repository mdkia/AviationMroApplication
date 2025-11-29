package com.aviation.mro.modules.sales.controller;

import com.aviation.mro.modules.sales.domain.dto.CustomerRequest;
import com.aviation.mro.modules.sales.domain.dto.CustomerResponse;
import com.aviation.mro.modules.sales.service.CustomerService;
import com.aviation.mro.shared.common.ApiResponse;
import com.aviation.mro.shared.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sales/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "APIs for managing customers")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "Create a new customer")
    public ResponseEntity<ApiResponse> createCustomer(
            @Valid @RequestBody CustomerRequest request,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        CustomerResponse response = customerService.createCustomer(request, username);
        return ResponseEntity.ok(ApiResponse.success("Customer created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all customers")
    public ResponseEntity<ApiResponse> getAllCustomers() {
        List<CustomerResponse> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(ApiResponse.success("Customers retrieved successfully", customers));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<ApiResponse> getCustomerById(@PathVariable Long id) {
        CustomerResponse customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(ApiResponse.success("Customer retrieved successfully", customer));
    }

    @GetMapping("/code/{customerCode}")
    @Operation(summary = "Get customer by code")
    public ResponseEntity<ApiResponse> getCustomerByCode(@PathVariable String customerCode) {
        CustomerResponse customer = customerService.getCustomerByCode(customerCode);
        return ResponseEntity.ok(ApiResponse.success("Customer retrieved successfully", customer));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer")
    public ResponseEntity<ApiResponse> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        CustomerResponse customer = customerService.updateCustomer(id, request, username);
        return ResponseEntity.ok(ApiResponse.success("Customer updated successfully", customer));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate customer")
    public ResponseEntity<ApiResponse> deactivateCustomer(
            @PathVariable Long id,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        customerService.deactivateCustomer(id, username);
        return ResponseEntity.ok(ApiResponse.success("Customer deactivated successfully", null));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active customers")
    public ResponseEntity<ApiResponse> getActiveCustomers() {
        List<CustomerResponse> customers = customerService.getActiveCustomers();
        return ResponseEntity.ok(ApiResponse.success("Active customers retrieved successfully", customers));
    }

    @GetMapping("/search")
    @Operation(summary = "Search customers by company name")
    public ResponseEntity<ApiResponse> searchCustomersByCompanyName(@RequestParam String companyName) {
        List<CustomerResponse> customers = customerService.searchCustomersByCompanyName(companyName);
        return ResponseEntity.ok(ApiResponse.success("Customers retrieved successfully", customers));
    }
}
