package com.aviation.mro.modules.warehouse.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryItemRequest {
    @NotNull
    private Long partId;

    @NotNull
    private Long storageLocationId;

    @NotNull
    @PositiveOrZero
    private Integer quantityOnHand;

    @PositiveOrZero
    private Integer quantityReserved;

    private Double unitCost;
    private LocalDate receiptDate;
    private LocalDate expirationDate;
    private String batchNumber;
    private String supplierInfo;
}