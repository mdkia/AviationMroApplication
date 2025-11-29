package com.aviation.mro.modules.sales.domain.dto;

import com.aviation.mro.modules.sales.domain.enums.CustomerType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomerRequest {

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String contactPerson;

    @Email(message = "Invalid email format")
    private String email;

    private String phone;
    private String website;

    @NotNull(message = "Customer type is required")
    private CustomerType customerType;

    // Address
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    // Financial
    private String taxId;
    private String vatNumber;
    private Double creditLimit;
}
