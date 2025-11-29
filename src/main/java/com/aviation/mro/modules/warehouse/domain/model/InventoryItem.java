package com.aviation.mro.modules.warehouse.domain.model;

import com.aviation.mro.modules.parts.domain.model.AircraftPart;
import com.aviation.mro.modules.warehouse.domain.enums.ApprovalStatus;
import com.aviation.mro.modules.warehouse.domain.enums.InventoryStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"part_id", "storage_location_id"}))
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id")
    private AircraftPart part;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_location_id")
    private StorageLocation storageLocation;

    private Integer quantityOnHand = 0; // موجودی فیزیکی
    private Integer quantityReserved = 0; // موجودی رزرو شده
    private Integer quantityAvailable = 0; // موجودی قابل دسترس

    private Double unitCost; // قیمت واحد

    private LocalDate receiptDate; // تاریخ دریافت
    private LocalDate expirationDate; // تاریخ انقضا (برای قطعات با Shelf Life)

    private String batchNumber;
    private String supplierInfo;

    @Enumerated(EnumType.STRING)
    private InventoryStatus status = InventoryStatus.ACTIVE;

    private LocalDateTime lastCountDate; // تاریخ آخرین شمارش
    private String lastCountBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // 🔐 فیلدهای جدید برای سیستم تأیید
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus = ApprovalStatus.NOT_REQUIRED;

    private String lastRequestedBy;      // آخرین درخواست دهنده
    private String lastApprovedBy;       // آخرین تأیید کننده
    private LocalDateTime lastRequestDate;
    private LocalDateTime lastApprovalDate;
    private String rejectionReason;      // دلیل رد درخواست

    // حداقل مقدار برای تأیید دو مرحله‌ای
    private Integer approvalThreshold = 0; // اگر موجودی از این کمتر شود، نیاز به تأیید دارد


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateAvailableQuantity();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateAvailableQuantity();
    }

    // محاسبه خودکار موجودی قابل دسترس
    private void calculateAvailableQuantity() {
        this.quantityAvailable = this.quantityOnHand - this.quantityReserved;
    }

    // Constructors
    public InventoryItem() {}

    public InventoryItem(AircraftPart part, StorageLocation storageLocation, Integer quantityOnHand) {
        this.part = part;
        this.storageLocation = storageLocation;
        this.quantityOnHand = quantityOnHand;
        calculateAvailableQuantity();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public AircraftPart getPart() { return part; }
    public void setPart(AircraftPart part) { this.part = part; }
    public StorageLocation getStorageLocation() { return storageLocation; }
    public void setStorageLocation(StorageLocation storageLocation) { this.storageLocation = storageLocation; }
    public Integer getQuantityOnHand() { return quantityOnHand; }
    public void setQuantityOnHand(Integer quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
        calculateAvailableQuantity();
    }
    public Integer getQuantityReserved() { return quantityReserved; }
    public void setQuantityReserved(Integer quantityReserved) {
        this.quantityReserved = quantityReserved;
        calculateAvailableQuantity();
    }
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

    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }
    public String getLastRequestedBy() { return lastRequestedBy; }
    public void setLastRequestedBy(String lastRequestedBy) { this.lastRequestedBy = lastRequestedBy; }
    public String getLastApprovedBy() { return lastApprovedBy; }
    public void setLastApprovedBy(String lastApprovedBy) { this.lastApprovedBy = lastApprovedBy; }
    public LocalDateTime getLastRequestDate() { return lastRequestDate; }
    public void setLastRequestDate(LocalDateTime lastRequestDate) { this.lastRequestDate = lastRequestDate; }
    public LocalDateTime getLastApprovalDate() { return lastApprovalDate; }
    public void setLastApprovalDate(LocalDateTime lastApprovalDate) { this.lastApprovalDate = lastApprovalDate; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public Integer getApprovalThreshold() { return approvalThreshold; }
    public void setApprovalThreshold(Integer approvalThreshold) { this.approvalThreshold = approvalThreshold; }

}