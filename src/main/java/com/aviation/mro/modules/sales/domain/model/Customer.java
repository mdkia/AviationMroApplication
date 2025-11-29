package com.aviation.mro.modules.sales.domain.model;

import com.aviation.mro.modules.sales.domain.enums.CustomerType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String customerCode; // Format: CUST-001

    @Column(nullable = false)
    private String companyName;

    private String contactPerson;
    private String email;
    private String phone;
    private String website;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
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
    private Double currentBalance;

    // Status
    private Boolean isActive = true;

    // Audit
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;
}
