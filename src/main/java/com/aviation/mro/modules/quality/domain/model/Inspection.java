package com.aviation.mro.modules.quality.domain.model;

import com.aviation.mro.modules.quality.domain.enums.DefectSeverity;
import com.aviation.mro.modules.quality.domain.enums.InspectionStatus;
import com.aviation.mro.modules.quality.domain.enums.ComplianceStatus;
import com.aviation.mro.modules.auth.model.User;
import com.aviation.mro.modules.parts.domain.model.AircraftPart;
import com.aviation.mro.modules.repair.domain.enums.RepairPriority;
import com.aviation.mro.modules.repair.domain.model.WorkOrder;
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
@Table(name = "inspections")
public class Inspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @Column(unique = true, nullable = false)
    private String inspectionNumber; // INS-2024-001

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_plan_id", nullable = false)
    private InspectionPlan inspectionPlan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InspectionStatus status = InspectionStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    private ComplianceStatus complianceStatus;

    // چه چیزی بازرسی می‌شود؟
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id")
    private AircraftPart part;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id")
    private WorkOrder workOrder;

    // اطلاعات بازرسی
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspector_id")
    private User inspector;

    private LocalDateTime scheduledDate;
    private LocalDateTime actualStartDate;
    private LocalDateTime actualEndDate;

    // نتایج
    private Integer totalChecks;
    private Integer passedChecks;
    private Integer failedChecks;
    private Double complianceRate; // درصد تطابق

    @Nationalized
    private String findings;

    @Nationalized
    private String recommendations;

    // Relationships
    @OneToMany(mappedBy = "inspection", cascade = CascadeType.ALL)
    private List<Defect> defects = new ArrayList<>();

    @OneToOne(mappedBy = "inspection")
    private NonConformanceReport nonConformanceReport;

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
    public void calculateResults() {
        this.totalChecks = defects.size();
        this.passedChecks = (int) defects.stream()
                .filter(defect -> defect.getComplianceStatus() == ComplianceStatus.COMPLIANT)
                .count();
        this.failedChecks = totalChecks - passedChecks;
        this.complianceRate = totalChecks > 0 ? (passedChecks * 100.0) / totalChecks : 0.0;

        this.complianceStatus = complianceRate >= 95 ? ComplianceStatus.COMPLIANT :
                complianceRate >= 80 ? ComplianceStatus.CONDITIONAL_APPROVAL :
                        ComplianceStatus.NON_COMPLIANT;
    }

    // Helper method to check if repair is needed
    public boolean requiresRepair() {
        return this.complianceStatus == ComplianceStatus.NON_COMPLIANT &&
                this.part != null;
    }

    // Helper method to get repair priority based on defects
    public RepairPriority getSuggestedRepairPriority() {
        if (this.defects == null || this.defects.isEmpty()) {
            return RepairPriority.LOW;
        }

        boolean hasCritical = this.defects.stream()
                .anyMatch(defect -> defect.getSeverity() == DefectSeverity.CRITICAL);

        boolean hasMajor = this.defects.stream()
                .anyMatch(defect -> defect.getSeverity() == DefectSeverity.MAJOR);

        if (hasCritical) {
            return RepairPriority.CRITICAL;
        } else if (hasMajor) {
            return RepairPriority.HIGH;
        } else {
            return RepairPriority.MEDIUM;
        }
    }
}
