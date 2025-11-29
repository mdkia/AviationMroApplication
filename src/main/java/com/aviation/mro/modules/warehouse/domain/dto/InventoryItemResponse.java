package com.aviation.mro.modules.warehouse.domain.dto;


import com.aviation.mro.modules.warehouse.domain.enums.InventoryStatus;
import com.aviation.mro.modules.warehouse.domain.model.InventoryItem;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPartId() { return partId; }
    public void setPartId(Long partId) { this.partId = partId; }
    public String getPartNumber() { return partNumber; }
    public void setPartNumber(String partNumber) { this.partNumber = partNumber; }
    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }
    public Long getStorageLocationId() { return storageLocationId; }
    public void setStorageLocationId(Long storageLocationId) { this.storageLocationId = storageLocationId; }
    public String getStorageLocationCode() { return storageLocationCode; }
    public void setStorageLocationCode(String storageLocationCode) { this.storageLocationCode = storageLocationCode; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    public Integer getQuantityOnHand() { return quantityOnHand; }
    public void setQuantityOnHand(Integer quantityOnHand) { this.quantityOnHand = quantityOnHand; }
    public Integer getQuantityReserved() { return quantityReserved; }
    public void setQuantityReserved(Integer quantityReserved) { this.quantityReserved = quantityReserved; }
    public Integer getQuantityAvailable() { return quantityAvailable; }
    public void setQuantityAvailable(Integer quantityAvailable) { this.quantityAvailable = quantityAvailable; }
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
    public InventoryStatus getStatus() { return status; }
    public void setStatus(InventoryStatus status) { this.status = status; }
    public LocalDateTime getLastCountDate() { return lastCountDate; }
    public void setLastCountDate(LocalDateTime lastCountDate) { this.lastCountDate = lastCountDate; }
    public String getLastCountBy() { return lastCountBy; }
    public void setLastCountBy(String lastCountBy) { this.lastCountBy = lastCountBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
