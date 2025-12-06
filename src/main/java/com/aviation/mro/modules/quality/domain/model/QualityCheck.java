package com.aviation.mro.modules.quality.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Nationalized;

@Data
@Entity
@Table(name = "quality_checks")
public class QualityCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @Column(nullable = false)
    private String checkCode; // QC-001

    @Nationalized
    @Column(nullable = false)
    private String description;

    @Nationalized
    private String acceptanceCriteria; // معیار پذیرش

    @Nationalized
    private String measurementMethod; // روش اندازه‌گیری

    @Nationalized
    private String toolsRequired; // ابزار مورد نیاز

    private Boolean isCritical = false; // آیا چک بحرانی است؟

    // ترتیب اجرا
    private Integer sequenceNumber;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_plan_id", nullable = false)
    private InspectionPlan inspectionPlan;
}