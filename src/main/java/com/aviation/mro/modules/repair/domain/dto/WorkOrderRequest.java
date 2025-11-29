package com.aviation.mro.modules.repair.domain.dto;

import com.aviation.mro.modules.repair.domain.enums.RepairPriority;
import com.aviation.mro.modules.repair.domain.enums.MaintenanceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class WorkOrderRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Priority is required")
    private RepairPriority priority;

    @NotNull(message = "Maintenance type is required")
    private MaintenanceType maintenanceType;

    // Aircraft Information
    @NotBlank(message = "Aircraft registration is required")
    private String aircraftRegistration;

    private String aircraftType;
    private String tailNumber;

    // Dates
    private LocalDateTime estimatedStartDate;
    private LocalDateTime estimatedCompletionDate;

    // Financials
    private Double estimatedCost;

    // Relationships
    private Long assignedTechnicianId;
    private List<Long> requiredPartIds;
}
