package com.aviation.mro.modules.repair.domain.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RepairTaskRequest {

    @NotBlank(message = "Task code is required")
    private String taskCode;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Work order ID is required")
    private Long workOrderId;

    private Long assignedTechnicianId;

    // Estimated values
    private Integer estimatedHours;
    private Double estimatedCost;

    // Task specific
    private String technicalReference;
    private String toolRequirements;
    private String safetyPrecautions;

    // Dates
    private LocalDateTime plannedStartDate;
    private LocalDateTime plannedEndDate;

    private List<Long> requiredPartIds;
}
