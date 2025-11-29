package com.aviation.mro.modules.repair.domain.dto;


import com.aviation.mro.modules.repair.domain.enums.WorkOrderStatus;
import com.aviation.mro.modules.repair.domain.enums.RepairPriority;
import com.aviation.mro.modules.repair.domain.enums.MaintenanceType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class WorkOrderResponse {
    private Long id;
    private String workOrderNumber;
    private String title;
    private String description;
    private WorkOrderStatus status;
    private RepairPriority priority;
    private MaintenanceType maintenanceType;

    // Aircraft Information
    private String aircraftRegistration;
    private String aircraftType;
    private String tailNumber;

    // Dates
    private LocalDateTime estimatedStartDate;
    private LocalDateTime estimatedCompletionDate;
    private LocalDateTime actualStartDate;
    private LocalDateTime actualCompletionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Financials
    private Double estimatedCost;
    private Double actualCost;

    // Assigned Personnel
    private String createdBy;
    private String assignedTechnician;
    private String approvedBy;

    // Tasks and Parts
    private List<RepairTaskResponse> tasks = new ArrayList<>();
    private List<PartInfo> requiredParts = new ArrayList<>();

    @Data
    public static class PartInfo {
        private Long id;
        private String partNumber;
        private String description;
        private String status;
    }
}