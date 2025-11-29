package com.aviation.mro.modules.parts.domain.model;
import com.aviation.mro.modules.parts.domain.enums.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "aircraft_parts")
public class AircraftPart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String partNumber;

    @NotBlank
    private String partName;

    private String description;

    @NotBlank
    @Column(unique = true)
    private String serialNumber;

    @NotBlank
    private String batchNumber;

    // Lifecycle status
    @Enumerated(EnumType.STRING)
    private ServiceabilityStatus serviceabilityStatus;

    @Enumerated(EnumType.STRING)
    private LocationStatus locationStatus;

    @Enumerated(EnumType.STRING)
    private CertificationStatus certificationStatus;

    @Column(name = "under_active_ad")
    private Boolean underActiveAD = false;
    private String adDetails;

    // Dates and usage
    private LocalDate manufactureDate;
    private LocalDate entryIntoServiceDate;
    private Integer totalFlightHours = 0;
    private Integer totalFlightCycles = 0;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (serviceabilityStatus == null) serviceabilityStatus = ServiceabilityStatus.SERVICEABLE;
        if (locationStatus == null) locationStatus = LocationStatus.IN_STOCK;
        if (certificationStatus == null) certificationStatus = CertificationStatus.NEEDS_CERTIFICATION;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public AircraftPart() {}

    public AircraftPart(String partNumber, String partName, String serialNumber, String batchNumber) {
        this.partNumber = partNumber;
        this.partName = partName;
        this.serialNumber = serialNumber;
        this.batchNumber = batchNumber;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPartNumber() { return partNumber; }
    public void setPartNumber(String partNumber) { this.partNumber = partNumber; }
    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    public ServiceabilityStatus getServiceabilityStatus() { return serviceabilityStatus; }
    public void setServiceabilityStatus(ServiceabilityStatus serviceabilityStatus) { this.serviceabilityStatus = serviceabilityStatus; }
    public LocationStatus getLocationStatus() { return locationStatus; }
    public void setLocationStatus(LocationStatus locationStatus) { this.locationStatus = locationStatus; }
    public CertificationStatus getCertificationStatus() { return certificationStatus; }
    public void setCertificationStatus(CertificationStatus certificationStatus) { this.certificationStatus = certificationStatus; }
    public Boolean getUnderActiveAD() { return underActiveAD; }
    public void setUnderActiveAD(Boolean underActiveAD) { this.underActiveAD = underActiveAD; }
    public String getAdDetails() { return adDetails; }
    public void setAdDetails(String adDetails) { this.adDetails = adDetails; }
    public LocalDate getManufactureDate() { return manufactureDate; }
    public void setManufactureDate(LocalDate manufactureDate) { this.manufactureDate = manufactureDate; }
    public LocalDate getEntryIntoServiceDate() { return entryIntoServiceDate; }
    public void setEntryIntoServiceDate(LocalDate entryIntoServiceDate) { this.entryIntoServiceDate = entryIntoServiceDate; }
    public Integer getTotalFlightHours() { return totalFlightHours; }
    public void setTotalFlightHours(Integer totalFlightHours) { this.totalFlightHours = totalFlightHours; }
    public Integer getTotalFlightCycles() { return totalFlightCycles; }
    public void setTotalFlightCycles(Integer totalFlightCycles) { this.totalFlightCycles = totalFlightCycles; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}