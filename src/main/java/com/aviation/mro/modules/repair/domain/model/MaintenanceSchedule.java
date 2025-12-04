package com.aviation.mro.modules.repair.domain.model;


import com.aviation.mro.modules.repair.domain.enums.MaintenanceType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "maintenance_schedules")
public class MaintenanceSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @Column(nullable = false)
    private String scheduleNumber;

    @Nationalized
    @Column(nullable = false)
    private String aircraftRegistration;

    @Nationalized
    @Column(nullable = false)
    private String aircraftType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceType maintenanceType;

    // Scheduling
    private LocalDateTime scheduledStartDate;
    private LocalDateTime scheduledEndDate;
    private LocalDateTime actualStartDate;
    private LocalDateTime actualEndDate;

    // Maintenance Metrics
    private Integer flightHours;
    private Integer flightCycles;
    private Integer daysSinceLastCheck;

    // Status
    private Boolean isCompleted = false;
    private Boolean isOverdue = false;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id")
    private WorkOrder workOrder;

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
