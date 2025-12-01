package com.aviation.mro.modules.quality.domain.model;

import com.aviation.mro.modules.quality.domain.enums.NCRStatus;
import com.aviation.mro.modules.quality.domain.enums.CorrectiveActionStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "non_conformance_reports")
public class NonConformanceReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String ncrNumber; // NCR-2024-001

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_id", nullable = false)
    private Inspection inspection;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NCRStatus status = NCRStatus.OPEN;

    @Enumerated(EnumType.STRING)
    private CorrectiveActionStatus correctiveActionStatus = CorrectiveActionStatus.PENDING;

    // اطلاعات NCR
    private String problemDescription;
    private String rootCause;
    private String immediateAction;
    private String correctiveAction;
    private String preventiveAction;

    // مسئولیت‌ها
    private String raisedBy;
    private String assignedTo;
    private String verifiedBy;

    // زمان‌بندی
    private LocalDateTime targetCompletionDate;
    private LocalDateTime actualCompletionDate;
    private LocalDateTime verificationDate;

    // نتایج
    private Boolean isEffective;
    private String verificationNotes;

    // Audit
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}