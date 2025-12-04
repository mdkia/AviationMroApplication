package com.aviation.mro.modules.parts.domain.model;

import com.aviation.mro.modules.parts.domain.enums.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.Nationalized;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "aircraft_parts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {}) // اگر روابط lazy دارید، اینجا exclude کنید
public class AircraftPart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @NotBlank
    @Column(unique = true, nullable = false, columnDefinition = "NVARCHAR(255)")
    private String partNumber;

    @Nationalized
    @NotBlank
    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    private String partName;

    @Nationalized
    @Column(columnDefinition = "NVARCHAR(2000)")
    private String description;

    @Nationalized
    @NotBlank
    @Column(unique = true, nullable = false, columnDefinition = "NVARCHAR(255)")
    private String serialNumber;

    @Nationalized
    @NotBlank
    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    private String batchNumber;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "NVARCHAR(50) DEFAULT 'SERVICEABLE'")
    private ServiceabilityStatus serviceabilityStatus = ServiceabilityStatus.SERVICEABLE;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "NVARCHAR(50) DEFAULT 'IN_STOCK'")
    private LocationStatus locationStatus = LocationStatus.IN_STOCK;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "NVARCHAR(50) DEFAULT 'NEEDS_CERTIFICATION'")
    private CertificationStatus certificationStatus = CertificationStatus.NEEDS_CERTIFICATION;

    @Column(name = "under_active_ad", columnDefinition = "BIT DEFAULT 0")
    private Boolean underActiveAD = false;

    @Nationalized
    @Column(columnDefinition = "NVARCHAR(1000)")
    private String adDetails;

    @Column(columnDefinition = "DATE")
    private LocalDate manufactureDate;

    @Column(columnDefinition = "DATE")
    private LocalDate entryIntoServiceDate;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer totalFlightHours = 0;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer totalFlightCycles = 0;

    @Column(columnDefinition = "DATETIME2")
    private LocalDateTime createdAt;

    @Column(columnDefinition = "DATETIME2")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // مقادیر پیش‌فرض اگر null هستند
        if (serviceabilityStatus == null) {
            serviceabilityStatus = ServiceabilityStatus.SERVICEABLE;
        }
        if (locationStatus == null) {
            locationStatus = LocationStatus.IN_STOCK;
        }
        if (certificationStatus == null) {
            certificationStatus = CertificationStatus.NEEDS_CERTIFICATION;
        }
        if (underActiveAD == null) {
            underActiveAD = false;
        }
    }


    // Constructor سفارشی (اختیاری)
    public AircraftPart(String partNumber, String partName, String serialNumber, String batchNumber) {
        this.partNumber = partNumber;
        this.partName = partName;
        this.serialNumber = serialNumber;
        this.batchNumber = batchNumber;
        this.serviceabilityStatus = ServiceabilityStatus.SERVICEABLE;
        this.locationStatus = LocationStatus.IN_STOCK;
        this.certificationStatus = CertificationStatus.NEEDS_CERTIFICATION;
        this.underActiveAD = false;
        this.totalFlightHours = 0;
        this.totalFlightCycles = 0;
    }
}