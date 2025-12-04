package com.aviation.mro.modules.parts.domain.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class AircraftPartDetailDto {
    private Long id;
    private String partNumber;
    private String partName;
    private String description;
    private String serialNumber;
    private String batchNumber;
    private String serviceabilityStatus;
    private String locationStatus;
    private String certificationStatus;
    private Boolean underActiveAD;
    private String adDetails;
    private LocalDate manufactureDate;
    private LocalDate entryIntoServiceDate;
    private Integer totalFlightHours;
    private Integer totalFlightCycles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}