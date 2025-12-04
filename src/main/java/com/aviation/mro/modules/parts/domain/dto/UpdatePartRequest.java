package com.aviation.mro.modules.parts.domain.dto;


import jakarta.validation.constraints.Min;
import lombok.Data;
import java.math.BigDecimal;


import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdatePartRequest {
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
}