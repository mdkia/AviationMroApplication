package com.aviation.mro.modules.quality.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "quality_standards")
public class QualityStandard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @Column(unique = true, nullable = false)
    private String standardCode; // EASA-145, FAA-PART-145, ISO-9001

    @Nationalized
    @Column(nullable = false)
    private String standardName;

    @Nationalized
    private String description;

    @Nationalized
    private String version;

    @Nationalized
    private String issuingAuthority; // EASA, FAA, ISO, etc.

    private LocalDateTime effectiveDate;
    private LocalDateTime expiryDate;

    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Nationalized
    private String createdBy;

    @Nationalized
    private String updatedBy;
}