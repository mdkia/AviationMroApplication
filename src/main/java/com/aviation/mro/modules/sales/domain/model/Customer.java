package com.aviation.mro.modules.sales.domain.model;

import com.aviation.mro.modules.sales.domain.enums.CustomerType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @Column(unique = true, nullable = false)
    private String customerCode; // Format: CUST-001

    @Nationalized
    @Column(nullable = false)
    private String companyName;

    @Nationalized
    private String contactPerson;

    @Nationalized
    private String email;

    @Nationalized
    private String phone;

    @Nationalized
    private String website;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerType customerType;

    // Address
    @Nationalized
    private String address;

    @Nationalized
    private String city;

    @Nationalized
    private String state;

    @Nationalized
    private String country;

    @Nationalized
    private String postalCode;

    // Financial
    @Nationalized
    private String taxId;

    @Nationalized
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

    @Nationalized
    private String createdBy;

    @Nationalized
    private String updatedBy;
}
