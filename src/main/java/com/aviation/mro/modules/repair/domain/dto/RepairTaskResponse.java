package com.aviation.mro.modules.repair.domain.dto;


import com.aviation.mro.modules.repair.domain.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class RepairTaskResponse {
    private Long id;
    private String taskCode;
    private String title;
    private String description;
    private TaskStatus status;

    private Long workOrderId;
    private String workOrderNumber;

    // Estimated vs Actual
    private Integer estimatedHours;
    private Integer actualHours;
    private Double estimatedCost;
    private Double actualCost;

    // Task specific
    private String technicalReference;
    private String toolRequirements;
    private String safetyPrecautions;

    // Dates
    private LocalDateTime plannedStartDate;
    private LocalDateTime plannedEndDate;
    private LocalDateTime actualStartDate;
    private LocalDateTime actualEndDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Assigned technician
    private String assignedTechnician;

    // Required parts
    private List<PartInfo> requiredParts = new ArrayList<>();

    @Data
    public static class PartInfo {
        private Long id;
        private String partNumber;
        private String description;
    }
}
