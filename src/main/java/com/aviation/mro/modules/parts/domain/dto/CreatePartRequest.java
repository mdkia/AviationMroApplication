package com.aviation.mro.modules.parts.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreatePartRequest {
    @NotBlank(message = "شماره قطعه الزامی است")
    private String partNumber;

    @NotBlank(message = "نام قطعه الزامی است")
    private String partName;

    private String description;

    @NotBlank(message = "شماره سریال الزامی است")
    private String serialNumber;

    @NotBlank(message = "شماره بچ الزامی است")
    private String batchNumber;

    private String serviceabilityStatus; // "SERVICEABLE", "UNSERVICEABLE", etc.
    private String locationStatus; // "IN_STOCK", "INSTALLED", etc.
    private String certificationStatus; // "CERTIFIED", "NEEDS_CERTIFICATION", etc.

    private Boolean underActiveAD = false;
    private String adDetails;

    private LocalDate manufactureDate;
    private LocalDate entryIntoServiceDate;
    private Integer totalFlightHours = 0;
    private Integer totalFlightCycles = 0;
}