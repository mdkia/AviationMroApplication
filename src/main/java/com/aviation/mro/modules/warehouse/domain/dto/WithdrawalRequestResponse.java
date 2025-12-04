package com.aviation.mro.modules.warehouse.domain.dto;

import com.aviation.mro.modules.warehouse.domain.enums.WithdrawalStatus;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawalRequestResponse {
    private Long id;
    private Long inventoryItemId;
    private String partNumber;
    private String partName;
    private String storageLocationCode;
    private Integer requestedQuantity;
    private String purpose;
    private String workOrderNumber;
    private String referenceNumber;
    private String requestedBy;
    private LocalDateTime requestDate;
    private String requestNotes;
    private String approvedBy;
    private LocalDateTime approvalDate;
    private String approvalNotes;
    private String rejectionReason;
    private WithdrawalStatus status;
    private LocalDateTime createdAt;
}
