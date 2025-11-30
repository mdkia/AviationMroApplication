package com.aviation.mro.modules.accounting.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class JournalEntryRequest {

    @NotNull(message = "Entry date is required")
    private LocalDateTime entryDate;

    private String referenceNumber;
    private String description;
    private String notes;

    private String sourceModule; // SALES, REPAIR, PURCHASE, MANUAL
    private Long sourceId;

    @NotNull(message = "At least one journal item is required")
    private List<JournalItemRequest> items = new ArrayList<>();
}
