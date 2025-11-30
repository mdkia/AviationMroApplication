package com.aviation.mro.modules.accounting.domain.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class JournalEntryResponse {
    private Long id;
    private String entryNumber;
    private LocalDateTime entryDate;
    private String referenceNumber;
    private String description;
    private String notes;
    private Double totalDebit;
    private Double totalCredit;
    private Boolean isPosted;
    private LocalDateTime postedDate;
    private String sourceModule;
    private Long sourceId;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<JournalItemResponse> items = new ArrayList<>();

    @Data
    public static class JournalItemResponse {
        private Long id;
        private Long accountId;
        private String accountCode;
        private String accountName;
        private Double debitAmount;
        private Double creditAmount;
        private String description;
        private String reference;
        private Integer lineNumber;
    }
}
