package com.aviation.mro.modules.warehouse.domain.model;

import com.aviation.mro.modules.warehouse.domain.enums.WithdrawalStatus;
import com.aviation.mro.modules.warehouse.domain.model.InventoryItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_withdrawal_requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockWithdrawalRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id")
    private InventoryItem inventoryItem;

    private Integer requestedQuantity;
    private String purpose; // هدف خروج: REPAIR, SALES, SAMPLE, SCRAP, etc.

    @Nationalized
    private String workOrderNumber; // شماره دستور کار مرتبط

    @Nationalized
    private String referenceNumber; // شماره مرجع داخلی

    // اطلاعات درخواست
    @Nationalized
    private String requestedBy;
    private LocalDateTime requestDate;

    @Nationalized
    private String requestNotes;

    // اطلاعات تأیید
    @Nationalized
    private String approvedBy;
    private LocalDateTime approvalDate;
    @Nationalized
    private String approvalNotes;

    @Nationalized
    private String rejectionReason;

    @Enumerated(EnumType.STRING)
    private WithdrawalStatus status = WithdrawalStatus.PENDING;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (requestDate == null) {
            requestDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public StockWithdrawalRequest(InventoryItem inventoryItem, Integer requestedQuantity,
                                  String purpose, String requestedBy) {
        this.inventoryItem = inventoryItem;
        this.requestedQuantity = requestedQuantity;
        this.purpose = purpose;
        this.requestedBy = requestedBy;
        this.requestDate = LocalDateTime.now();
    }
}
