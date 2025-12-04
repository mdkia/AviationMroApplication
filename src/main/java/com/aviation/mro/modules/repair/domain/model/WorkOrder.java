package com.aviation.mro.modules.repair.domain.model;


import com.aviation.mro.modules.repair.domain.enums.*;
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
@Table(name = "work_orders")
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @Column(unique = true, nullable = false)
    private String workOrderNumber; // Format: WO-YYYY-MM-001

    @Nationalized
    @Column(nullable = false)
    private String title;

    @Nationalized
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkOrderStatus status = WorkOrderStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepairPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceType maintenanceType;

    // Aircraft Information
    @Nationalized
    private String aircraftRegistration;
    @Nationalized
    private String aircraftType;
    @Nationalized
    private String tailNumber;

    // Dates
    private LocalDateTime estimatedStartDate;
    private LocalDateTime estimatedCompletionDate;
    private LocalDateTime actualStartDate;
    private LocalDateTime actualCompletionDate;

    // Financials
    private Double estimatedCost;
    private Double actualCost;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_technician_id")
    private User assignedTechnician;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_id")
    private User approvedBy;

    @ManyToMany
    @JoinTable(
            name = "work_order_parts",
            joinColumns = @JoinColumn(name = "work_order_id"),
            inverseJoinColumns = @JoinColumn(name = "part_id")
    )
    private List<AircraftPart> requiredParts = new ArrayList<>();

    @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL)
    private List<RepairTask> tasks = new ArrayList<>();

    // Audit
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Nationalized
    private String createdByUser;

    @Nationalized
    private String updatedByUser;

    // Helper methods
    public void addTask(RepairTask task) {
        tasks.add(task);
        task.setWorkOrder(this);
    }

    public void removeTask(RepairTask task) {
        tasks.remove(task);
        task.setWorkOrder(null);
    }
}
