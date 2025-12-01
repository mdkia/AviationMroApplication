package com.aviation.mro.modules.quality.domain.dto;

import com.aviation.mro.modules.quality.domain.enums.InspectionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InspectionPlanRequest {

    @NotBlank(message = "Plan title is required")
    private String title;

    private String description;

    @NotNull(message = "Inspection type is required")
    private InspectionType inspectionType;

    private String applicableStandards;
    private Integer inspectionFrequencyDays;
    private Integer sampleSize;
    private Long qualityStandardId;

    @NotNull(message = "At least one quality check is required")
    private List<QualityCheckRequest> checkpoints = new ArrayList<>();
}
