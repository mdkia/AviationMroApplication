package com.aviation.mro.modules.quality.domain.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "quality_checks")
public class QualityCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String checkCode; // QC-001

    @Column(nullable = false)
    private String description;

    private String acceptanceCriteria; // معیار پذیرش
    private String measurementMethod; // روش اندازه‌گیری
    private String toolsRequired; // ابزار مورد نیاز

    private Boolean isCritical = false; // آیا چک بحرانی است؟

    // ترتیب اجرا
    private Integer sequenceNumber;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_plan_id", nullable = false)
    private InspectionPlan inspectionPlan;
}
