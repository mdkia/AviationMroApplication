package com.aviation.mro.modules.quality.domain.model;

import com.aviation.mro.modules.quality.domain.enums.InspectionType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "inspection_plans")
public class InspectionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @Column(unique = true, nullable = false)
    private String planNumber; // QP-2024-001

    @Nationalized
    @Column(nullable = false)
    private String title;

    @Nationalized
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InspectionType inspectionType;

    // استانداردهای قابل اجرا
    @Nationalized
    private String applicableStandards; // EASA Part 145, FAA Part 145, etc.

    // فرکانس بازرسی
    private Integer inspectionFrequencyDays;
    private Integer sampleSize; // برای نمونه‌برداری

    // فعال/غیرفعال
    private Boolean isActive = true;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quality_standard_id")
    private QualityStandard qualityStandard;

    @OneToMany(mappedBy = "inspectionPlan", cascade = CascadeType.ALL)
    private List<QualityCheck> checkpoints = new ArrayList<>();

    // Audit
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Nationalized
    private String createdBy;

    @Nationalized
    private String updatedBy;

    // Helper methods
    public void addCheckpoint(QualityCheck checkpoint) {
        checkpoints.add(checkpoint);
        checkpoint.setInspectionPlan(this);
    }
}
