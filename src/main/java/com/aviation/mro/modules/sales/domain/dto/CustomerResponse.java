package com.aviation.mro.modules.sales.domain.dto;

import com.aviation.mro.modules.sales.domain.enums.CustomerType;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class CustomerResponse {
    private Long id;
    private String customerCode;
    private String companyName;
    private String contactPerson;
    private String email;
    private String phone;
    private String website;
    private CustomerType customerType;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String taxId;
    private String vatNumber;
    private Double creditLimit;
    private Double currentBalance;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
