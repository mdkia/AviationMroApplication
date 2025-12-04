package com.aviation.mro.modules.accounting.domain.model;

import com.aviation.mro.modules.accounting.domain.enums.AccountType;
import com.aviation.mro.modules.accounting.domain.enums.AccountCategory;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @Column(unique = true, nullable = false,length = 100)
    private String accountCode; // فرمت: 1010, 1020, etc.

    @Nationalized
    @Column(nullable = false)
    private String accountName;

    @Nationalized
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountCategory category;

    // حساب کل/معین/تفصیلی
    private Boolean isParent = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_account_id")
    private Account parentAccount;

    // مانده حساب
    private Double openingBalance = 0.0;
    private Double currentBalance = 0.0;
    private Double debitTotal = 0.0;
    private Double creditTotal = 0.0;

    // وضعیت
    private Boolean isActive = true;

    // برای گزارش‌گیری
    private Integer displayOrder;
    private Integer level; // سطح در ساختار درختی

    // Audit
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Nationalized
    private String createdBy;
    @Nationalized
    private String updatedBy;

    // Helper methods
    public void updateBalance(Double debitAmount, Double creditAmount) {
        this.debitTotal += debitAmount != null ? debitAmount : 0.0;
        this.creditTotal += creditAmount != null ? creditAmount : 0.0;

        if (this.accountType == AccountType.ASSET || this.accountType == AccountType.EXPENSE) {
            this.currentBalance = this.openingBalance + this.debitTotal - this.creditTotal;
        } else {
            this.currentBalance = this.openingBalance + this.creditTotal - this.debitTotal;
        }
    }

    public String getFullAccountCode() {
        if (parentAccount != null) {
            return parentAccount.getAccountCode() + "." + accountCode;
        }
        return accountCode;
    }
}