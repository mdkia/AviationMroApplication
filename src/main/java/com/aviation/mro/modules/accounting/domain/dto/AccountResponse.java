package com.aviation.mro.modules.accounting.domain.dto;

import com.aviation.mro.modules.accounting.domain.enums.AccountType;
import com.aviation.mro.modules.accounting.domain.enums.AccountCategory;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AccountResponse {
    private Long id;
    private String accountCode;
    private String fullAccountCode;
    private String accountName;
    private String description;
    private AccountType accountType;
    private AccountCategory category;
    private Boolean isParent;
    private Long parentAccountId;
    private String parentAccountName;
    private Double openingBalance;
    private Double currentBalance;
    private Double debitTotal;
    private Double creditTotal;
    private Boolean isActive;
    private Integer displayOrder;
    private Integer level;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
