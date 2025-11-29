package com.aviation.mro.modules.repair.domain.dto;


import com.aviation.mro.modules.repair.domain.enums.MaintenanceType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MaintenanceScheduleResponse {
    private Long id;
    private String scheduleNumber;
    private String aircraftRegistration;
    private String aircraftType;
    private MaintenanceType maintenanceType;
    private LocalDateTime scheduledStartDate;
    private LocalDateTime scheduledEndDate;
    private LocalDateTime actualStartDate;
    private LocalDateTime actualEndDate;
    private Integer flightHours;
    private Integer flightCycles;
    private Integer daysSinceLastCheck;
    private Boolean isCompleted;
    private Boolean isOverdue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Linked work order
    private Long workOrderId;
    private String workOrderNumber;
}
