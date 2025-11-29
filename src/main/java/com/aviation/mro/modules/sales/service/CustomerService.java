package com.aviation.mro.modules.sales.service;

import com.aviation.mro.modules.sales.domain.dto.CustomerRequest;
import com.aviation.mro.modules.sales.domain.dto.CustomerResponse;
import com.aviation.mro.modules.sales.domain.model.Customer;
import com.aviation.mro.modules.sales.repository.CustomerRepository;
import com.aviation.mro.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request, String username) {
        // Generate customer code
        String customerCode = generateCustomerCode();

        Customer customer = new Customer();
        customer.setCustomerCode(customerCode);
        customer.setCompanyName(request.getCompanyName());
        customer.setContactPerson(request.getContactPerson());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setWebsite(request.getWebsite());
        customer.setCustomerType(request.getCustomerType());
        customer.setAddress(request.getAddress());
        customer.setCity(request.getCity());
        customer.setState(request.getState());
        customer.setCountry(request.getCountry());
        customer.setPostalCode(request.getPostalCode());
        customer.setTaxId(request.getTaxId());
        customer.setVatNumber(request.getVatNumber());
        customer.setCreditLimit(request.getCreditLimit());
        customer.setCurrentBalance(0.0);
        customer.setCreatedBy(username);

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created: {} by user: {}", customerCode, username);

        return mapToCustomerResponse(savedCustomer);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToCustomerResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + id));
        return mapToCustomerResponse(customer);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByCode(String customerCode) {
        Customer customer = customerRepository.findByCustomerCode(customerCode)
                .orElseThrow(() -> new NotFoundException("Customer not found with code: " + customerCode));
        return mapToCustomerResponse(customer);
    }

    @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerRequest request, String username) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + id));

        customer.setCompanyName(request.getCompanyName());
        customer.setContactPerson(request.getContactPerson());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setWebsite(request.getWebsite());
        customer.setCustomerType(request.getCustomerType());
        customer.setAddress(request.getAddress());
        customer.setCity(request.getCity());
        customer.setState(request.getState());
        customer.setCountry(request.getCountry());
        customer.setPostalCode(request.getPostalCode());
        customer.setTaxId(request.getTaxId());
        customer.setVatNumber(request.getVatNumber());
        customer.setCreditLimit(request.getCreditLimit());
        customer.setUpdatedBy(username);

        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Customer updated: {} by user: {}", customer.getCustomerCode(), username);

        return mapToCustomerResponse(updatedCustomer);
    }

    @Transactional
    public void deactivateCustomer(Long id, String username) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + id));

        customer.setIsActive(false);
        customer.setUpdatedBy(username);
        customerRepository.save(customer);
        log.info("Customer deactivated: {} by user: {}", customer.getCustomerCode(), username);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> getActiveCustomers() {
        return customerRepository.findByIsActiveTrue().stream()
                .map(this::mapToCustomerResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> searchCustomersByCompanyName(String companyName) {
        return customerRepository.findByCompanyNameContainingIgnoreCase(companyName).stream()
                .map(this::mapToCustomerResponse)
                .collect(Collectors.toList());
    }

    // Helper method to generate customer code
    private String generateCustomerCode() {
        long count = customerRepository.count() + 1;
        return "CUST-" + String.format("%03d", count);
    }

    // Mapping helper
    private CustomerResponse mapToCustomerResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setCustomerCode(customer.getCustomerCode());
        response.setCompanyName(customer.getCompanyName());
        response.setContactPerson(customer.getContactPerson());
        response.setEmail(customer.getEmail());
        response.setPhone(customer.getPhone());
        response.setWebsite(customer.getWebsite());
        response.setCustomerType(customer.getCustomerType());
        response.setAddress(customer.getAddress());
        response.setCity(customer.getCity());
        response.setState(customer.getState());
        response.setCountry(customer.getCountry());
        response.setPostalCode(customer.getPostalCode());
        response.setTaxId(customer.getTaxId());
        response.setVatNumber(customer.getVatNumber());
        response.setCreditLimit(customer.getCreditLimit());
        response.setCurrentBalance(customer.getCurrentBalance());
        response.setIsActive(customer.getIsActive());
        response.setCreatedAt(customer.getCreatedAt());
        response.setUpdatedAt(customer.getUpdatedAt());
        return response;
    }
}
