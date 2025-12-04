package com.aviation.mro.modules.quality.domain.model;


import com.aviation.mro.modules.quality.domain.enums.DefectSeverity;
import com.aviation.mro.modules.quality.domain.enums.ComplianceStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Nationalized;

@Data
@Entity
@Table(name = "defects")
public class Defect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_id", nullable = false)
    private Inspection inspection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quality_check_id", nullable = false)
    private QualityCheck qualityCheck;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplianceStatus complianceStatus;

    @Enumerated(EnumType.STRING)
    private DefectSeverity severity;

    @Nationalized
    private String actualValue; // مقدار اندازه‌گیری شده

    @Nationalized
    private String deviation; // انحراف از استاندارد

    @Nationalized
    private String notes;

    // تصاویر/مستندات
    @Nationalized
    private String evidencePhotos;

    @Nationalized
    private String supportingDocuments;
}
