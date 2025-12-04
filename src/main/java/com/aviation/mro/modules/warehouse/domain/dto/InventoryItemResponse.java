package com.aviation.mro.modules.warehouse.domain.dto;

import com.aviation.mro.modules.warehouse.domain.enums.InventoryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryItemResponse {
    private Long id;
    private Long partId;
    private String partNumber;
    private String partName;
    private Long storageLocationId;
    private String storageLocationCode;
    private Long warehouseId;
    private String warehouseName;
    private Integer quantityOnHand;
    private Integer quantityReserved;
    private Integer quantityAvailable;
    private Double unitCost;
    private LocalDate receiptDate;
    private LocalDate expirationDate;
    private String batchNumber;
    private String supplierInfo;
    private InventoryStatus status;
    private LocalDateTime lastCountDate;
    private String lastCountBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
