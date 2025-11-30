package com.aviation.mro.modules.quality.domain.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class QualityStandardResponse {
    private Long id;
    private String standardCode;
    private String standardName;
    private String description;
    private String version;
    private String issuingAuthority;
    private LocalDateTime effectiveDate;
    private LocalDateTime expiryDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}