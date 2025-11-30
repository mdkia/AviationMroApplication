package com.aviation.mro.modules.accounting.domain.dto;

import com.aviation.mro.modules.accounting.domain.enums.AccountType;
import com.aviation.mro.modules.accounting.domain.enums.AccountCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccountRequest {

    @NotBlank(message = "Account code is required")
    private String accountCode;

    @NotBlank(message = "Account name is required")
    private String accountName;

    private String description;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @NotNull(message = "Account category is required")
    private AccountCategory category;

    private Boolean isParent = false;

    private Long parentAccountId;

    private Double openingBalance = 0.0;

    private Integer displayOrder;
    private Integer level = 1;
}
