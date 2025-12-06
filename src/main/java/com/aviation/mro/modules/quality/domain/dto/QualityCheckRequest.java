package com.aviation.mro.modules.quality.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QualityCheckRequest {

    @NotBlank(message = "Check code is required")
    private String checkCode;

    @NotBlank(message = "Description is required")
    private String description;

    private String acceptanceCriteria;
    private String measurementMethod;
    private String toolsRequired;
    private Boolean isCritical = false;
    private Integer sequenceNumber;
}