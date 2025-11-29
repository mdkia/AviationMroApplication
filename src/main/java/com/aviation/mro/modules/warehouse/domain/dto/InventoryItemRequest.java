package com.aviation.mro.modules.warehouse.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

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

    // Constructors
    public InventoryItemRequest() {}

    // Getters and Setters
    public Long getPartId() { return partId; }
    public void setPartId(Long partId) { this.partId = partId; }
    public Long getStorageLocationId() { return storageLocationId; }
    public void setStorageLocationId(Long storageLocationId) { this.storageLocationId = storageLocationId; }
    public Integer getQuantityOnHand() { return quantityOnHand; }
    public void setQuantityOnHand(Integer quantityOnHand) { this.quantityOnHand = quantityOnHand; }
    public Integer getQuantityReserved() { return quantityReserved; }
    public void setQuantityReserved(Integer quantityReserved) { this.quantityReserved = quantityReserved; }
    public Double getUnitCost() { return unitCost; }
    public void setUnitCost(Double unitCost) { this.unitCost = unitCost; }
    public LocalDate getReceiptDate() { return receiptDate; }
    public void setReceiptDate(LocalDate receiptDate) { this.receiptDate = receiptDate; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    public String getSupplierInfo() { return supplierInfo; }
    public void setSupplierInfo(String supplierInfo) { this.supplierInfo = supplierInfo; }
}