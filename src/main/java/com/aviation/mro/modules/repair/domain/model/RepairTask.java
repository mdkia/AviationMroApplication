package com.aviation.mro.modules.repair.domain.model;


import com.aviation.mro.modules.repair.domain.enums.TaskStatus;
import com.aviation.mro.modules.auth.model.User;
import com.aviation.mro.modules.parts.domain.model.AircraftPart;
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
@Table(name = "repair_tasks")
public class RepairTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @Column(nullable = false)
    private String taskCode; // MRO standard task codes

    @Nationalized
    @Column(nullable = false)
    private String title;

    @Nationalized
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.PENDING;

    // Estimated vs Actual
    private Integer estimatedHours;
    private Integer actualHours;

    private Double estimatedCost;
    private Double actualCost;

    // Task specific
    @Nationalized
    private String technicalReference; // AMM, CMM references

    @Nationalized
    private String toolRequirements;
    @Nationalized
    private String safetyPrecautions;

    // Dates
    private LocalDateTime plannedStartDate;
    private LocalDateTime plannedEndDate;
    private LocalDateTime actualStartDate;
    private LocalDateTime actualEndDate;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_technician_id")
    private User assignedTechnician;

    @ManyToMany
    @JoinTable(
            name = "task_required_parts",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "part_id")
    )
    private List<AircraftPart> requiredParts = new ArrayList<>();

    // Audit
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Nationalized
    private String createdByUser;
    @Nationalized
    private String updatedByUser;
}
