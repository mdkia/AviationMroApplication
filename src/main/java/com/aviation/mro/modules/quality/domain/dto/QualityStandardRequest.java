package com.aviation.mro.modules.quality.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QualityStandardRequest {

    @NotBlank(message = "Standard code is required")
    private String standardCode;

    @NotBlank(message = "Standard name is required")
    private String standardName;

    private String description;
    private String version;
    private String issuingAuthority;

    @NotNull(message = "Effective date is required")
    private LocalDateTime effectiveDate;

    private LocalDateTime expiryDate;
}
