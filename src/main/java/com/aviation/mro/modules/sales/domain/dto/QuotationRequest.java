package com.aviation.mro.modules.sales.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class QuotationRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private LocalDateTime expiryDate;

    private Double taxAmount = 0.0;
    private Double discountAmount = 0.0;

    private String termsAndConditions;
    private String notes;

    @NotNull(message = "At least one item is required")
    private List<QuotationItemRequest> items = new ArrayList<>();
}
