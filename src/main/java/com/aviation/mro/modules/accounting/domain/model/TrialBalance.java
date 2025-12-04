package com.aviation.mro.modules.accounting.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "trial_balances")
public class TrialBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime asOfDate;

    // مبالغ کل
    private Double totalDebit = 0.0;
    private Double totalCredit = 0.0;
    private Double difference = 0.0;

    private Boolean isBalanced = false;

    // برای گزارش‌گیری
    @Nationalized
    private String periodCode;

    // Audit
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Nationalized
    private String generatedBy;
}