package com.aviation.mro.modules.repair.domain.dto;

import com.aviation.mro.modules.repair.domain.enums.MaintenanceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MaintenanceScheduleRequest {

    @NotBlank(message = "Aircraft registration is required")
    private String aircraftRegistration;

    @NotBlank(message = "Aircraft type is required")
    private String aircraftType;

    @NotNull(message = "Maintenance type is required")
    private MaintenanceType maintenanceType;

    private LocalDateTime scheduledStartDate;
    private LocalDateTime scheduledEndDate;

    private Integer flightHours;
    private Integer flightCycles;
    private Integer daysSinceLastCheck;
}
