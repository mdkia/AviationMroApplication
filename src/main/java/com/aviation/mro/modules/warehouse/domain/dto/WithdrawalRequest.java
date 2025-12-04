package com.aviation.mro.modules.warehouse.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawalRequest {
    @NotNull
    private Long inventoryItemId;

    @NotNull
    @Positive
    private Integer quantity;

    @NotNull
    private String purpose;

    private String workOrderNumber;
    private String referenceNumber;
    private String notes;
}
