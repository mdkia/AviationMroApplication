package com.aviation.mro.modules.accounting.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Data
@Entity
@Table(name = "financial_periods")
public class FinancialPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String periodCode; // فرمت: 2024-01

    @Column(nullable = false)
    private YearMonth yearMonth;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    // وضعیت دوره
    private Boolean isOpen = true;
    private Boolean isClosed = false;

    private LocalDateTime closedDate;
    private String closedBy;

    // مانده دوره
    private Double openingBalance;
    private Double closingBalance;

    // Audit
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;
}
