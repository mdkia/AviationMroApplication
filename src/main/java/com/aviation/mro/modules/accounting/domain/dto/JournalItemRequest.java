package com.aviation.mro.modules.accounting.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class JournalItemRequest {

    @NotNull(message = "Account ID is required")
    private Long accountId;

    @Positive(message = "Debit amount must be positive")
    private Double debitAmount = 0.0;

    @Positive(message = "Credit amount must be positive")
    private Double creditAmount = 0.0;

    private String description;
    private String reference;

    @NotNull(message = "Line number is required")
    private Integer lineNumber;
}