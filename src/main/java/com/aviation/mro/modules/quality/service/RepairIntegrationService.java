package com.aviation.mro.modules.quality.service;

import com.aviation.mro.modules.quality.domain.model.Inspection;
import com.aviation.mro.modules.quality.domain.enums.ComplianceStatus;
import com.aviation.mro.modules.quality.domain.enums.DefectSeverity;
import com.aviation.mro.modules.repair.domain.model.WorkOrder;
import com.aviation.mro.modules.repair.domain.enums.WorkOrderStatus;
import com.aviation.mro.modules.repair.domain.enums.RepairPriority;
import com.aviation.mro.modules.repair.domain.enums.MaintenanceType;
import com.aviation.mro.modules.repair.repository.WorkOrderRepository;
import com.aviation.mro.modules.auth.model.User;
import com.aviation.mro.modules.auth.repository.UserRepository;
import com.aviation.mro.modules.parts.domain.model.AircraftPart;
import com.aviation.mro.modules.parts.domain.enums.LocationStatus;
import com.aviation.mro.modules.parts.domain.enums.ServiceabilityStatus;
import com.aviation.mro.modules.parts.repository.AircraftPartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepairIntegrationService {

    private final WorkOrderRepository workOrderRepository;
    private final UserRepository userRepository;
    private final AircraftPartRepository aircraftPartRepository;

    @Transactional
    public WorkOrder createRepairWorkOrderFromInspection(Inspection inspection, String username) {
        // Validate inputs
        if (inspection == null) {
            throw new IllegalArgumentException("Inspection cannot be null");
        }

        if (inspection.getPart() == null) {
            throw new IllegalStateException("Cannot create repair work order: Inspection has no associated part");
        }

        if (inspection.getComplianceStatus() != ComplianceStatus.NON_COMPLIANT) {
            throw new IllegalStateException("Cannot create repair work order: Inspection passed compliance check");
        }

        // Generate work order number
        String workOrderNumber = generateRepairOrderNumber();

        // Find an available technician
        User assignedTechnician = findAvailableTechnician();
        if (assignedTechnician == null) {
            throw new IllegalStateException("No available technician found for repair work order");
        }

        // Get the current user (creator)
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Create work order
        WorkOrder workOrder = new WorkOrder();
        workOrder.setWorkOrderNumber(workOrderNumber);
        workOrder.setTitle(generateWorkOrderTitle(inspection));
        workOrder.setDescription(generateWorkOrderDescription(inspection));
        workOrder.setStatus(WorkOrderStatus.PENDING_APPROVAL);
        workOrder.setPriority(determineRepairPriority(inspection));
        workOrder.setMaintenanceType(determineMaintenanceType(inspection));

        // aircraft fields: for component repairs we set a generic value
        workOrder.setAircraftRegistration("N/A - Component Repair");
        workOrder.setAircraftType("Component");
        workOrder.setTailNumber(null);

        // Dates & cost
        workOrder.setEstimatedStartDate(LocalDateTime.now().plusDays(1));
        workOrder.setEstimatedCompletionDate(LocalDateTime.now().plusDays(7));
        workOrder.setEstimatedCost(calculateEstimatedRepairCost(inspection));

        // Assign personnel and audit
        workOrder.setAssignedTechnician(assignedTechnician);
        workOrder.setCreatedBy(currentUser);
        workOrder.setCreatedByUser(username != null ? username : (currentUser.getUsername()));

        // Link required parts (WorkOrder.requiredParts is initialized in entity)
        AircraftPart part = inspection.getPart();
        if (part != null) {
            workOrder.getRequiredParts().add(part);
        }

        WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);

        // Update the part status (persist change)
        updatePartStatusForRepair(part);

        log.info("Repair work order created: {} for inspection: {} by user: {}",
                workOrderNumber, safeInspectionNumber(inspection), username);

        return savedWorkOrder;
    }

    private String generateRepairOrderNumber() {
        LocalDateTime now = LocalDateTime.now();
        String baseNumber = "RO-" + now.getYear() + "-" + String.format("%02d", now.getMonthValue());
        long count = workOrderRepository.count() + 1;
        return baseNumber + "-" + String.format("%03d", count);
    }

    private String generateWorkOrderTitle(Inspection inspection) {
        String partInfo = (inspection.getPart() != null && inspection.getPart().getPartNumber() != null) ?
                inspection.getPart().getPartNumber() : "Part";
        return "Corrective Repair - " + partInfo + " - " + safeInspectionNumber(inspection);
    }

    private String safeInspectionNumber(Inspection inspection) {
        try {
            return inspection.getInspectionNumber() != null ? inspection.getInspectionNumber() : "UNKNOWN_INS";
        } catch (Exception e) {
            return "UNKNOWN_INS";
        }
    }

    private String generateWorkOrderDescription(Inspection inspection) {
        StringBuilder description = new StringBuilder();
        description.append("Repair required based on failed inspection: ").append(safeInspectionNumber(inspection)).append("\n");

        if (inspection.getFindings() != null && !inspection.getFindings().isBlank()) {
            description.append("Findings: ").append(inspection.getFindings()).append("\n");
        }

        if (inspection.getRecommendations() != null && !inspection.getRecommendations().isBlank()) {
            description.append("Recommendations: ").append(inspection.getRecommendations()).append("\n");
        }

        // Add defect summary
        if (inspection.getDefects() != null && !inspection.getDefects().isEmpty()) {
            description.append("\nDefect Summary:\n");
            inspection.getDefects().stream()
                    .filter(Objects::nonNull)
                    .filter(defect -> defect.getComplianceStatus() == ComplianceStatus.NON_COMPLIANT)
                    .forEach(defect -> {
                        String qDesc = "Quality check";
                        try {
                            if (defect.getQualityCheck() != null && defect.getQualityCheck().getDescription() != null) {
                                qDesc = defect.getQualityCheck().getDescription();
                            }
                        } catch (Exception ignored) {}
                        description.append("- ").append(qDesc);
                        if (defect.getSeverity() != null) {
                            description.append(" (").append(defect.getSeverity()).append(")");
                        }
                        description.append("\n");
                    });
        }

        return description.toString();
    }

    private RepairPriority determineRepairPriority(Inspection inspection) {
        if (inspection.getDefects() == null || inspection.getDefects().isEmpty()) {
            return RepairPriority.MEDIUM;
        }

        boolean hasCritical = inspection.getDefects().stream()
                .filter(Objects::nonNull)
                .anyMatch(defect -> defect.getSeverity() == DefectSeverity.CRITICAL);

        boolean hasMajor = inspection.getDefects().stream()
                .filter(Objects::nonNull)
                .anyMatch(defect -> defect.getSeverity() == DefectSeverity.MAJOR);

        if (hasCritical) {
            return RepairPriority.CRITICAL;
        } else if (hasMajor) {
            return RepairPriority.HIGH;
        } else {
            return RepairPriority.MEDIUM;
        }
    }

    private MaintenanceType determineMaintenanceType(Inspection inspection) {
        // Simple heuristics: if part number contains ENG => engine repair; structural keyword in QC => structural repair
        try {
            if (inspection.getPart() != null && inspection.getPart().getPartNumber() != null &&
                    inspection.getPart().getPartNumber().toUpperCase().contains("ENG")) {
                return MaintenanceType.ENGINE_REPAIR;
            }
        } catch (Exception ignored) {}

        if (inspection.getDefects() != null) {
            boolean structural = inspection.getDefects().stream()
                    .filter(Objects::nonNull)
                    .anyMatch(defect -> {
                        try {
                            return defect.getQualityCheck() != null &&
                                    defect.getQualityCheck().getDescription() != null &&
                                    defect.getQualityCheck().getDescription().toLowerCase().contains("structural");
                        } catch (Exception e) {
                            return false;
                        }
                    });
            if (structural) return MaintenanceType.STRUCTURAL_REPAIR;
        }

        return MaintenanceType.COMPONENT_OVERHAUL;
    }

    private Double calculateEstimatedRepairCost(Inspection inspection) {
        double baseCost = 500.0; // Default base cost

        if (inspection.getDefects() != null) {
            long criticalDefects = inspection.getDefects().stream()
                    .filter(d -> d != null && d.getSeverity() == DefectSeverity.CRITICAL)
                    .count();

            long majorDefects = inspection.getDefects().stream()
                    .filter(d -> d != null && d.getSeverity() == DefectSeverity.MAJOR)
                    .count();

            baseCost += (criticalDefects * 1000.0); // $1000 per critical defect
            baseCost += (majorDefects * 500.0);     // $500 per major defect
        }

        // No unit cost available in AircraftPart model -> skip part-cost-based adjustment

        return baseCost;
    }

    private User findAvailableTechnician() {
        List<User> allUsers = userRepository.findAll();

        return allUsers.stream()
                .filter(Objects::nonNull)
                .filter(User::isEnabled) // matches your User model (enabled boolean)
                .filter(u -> u.getRoles() != null && u.getRoles().contains("TECHNICIAN"))
                .findFirst()
                .orElse(null);
    }

    private void updatePartStatusForRepair(AircraftPart part) {
        if (part == null) return;

        try {
            part.setServiceabilityStatus(ServiceabilityStatus.UNSERVICEABLE_REPAIRABLE);
        } catch (Exception ignored) {}

        try {
            part.setLocationStatus(LocationStatus.IN_REPAIR_SHOP);
        } catch (Exception ignored) {}

        aircraftPartRepository.save(part);

        log.debug("Part status updated for repair: {}", part.getPartNumber());
    }

    // Manual repair order creation
    @Transactional
    public WorkOrder createManualRepairOrder(Long partId, String description, RepairPriority priority, String username) {
        AircraftPart part = aircraftPartRepository.findById(partId)
                .orElseThrow(() -> new RuntimeException("Part not found with id: " + partId));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        User technician = findAvailableTechnician();
        if (technician == null) {
            throw new IllegalStateException("No available technician found");
        }

        String workOrderNumber = generateRepairOrderNumber();

        WorkOrder workOrder = new WorkOrder();
        workOrder.setWorkOrderNumber(workOrderNumber);
        workOrder.setTitle("Manual Repair - " + part.getPartNumber());
        workOrder.setDescription(description);
        workOrder.setStatus(WorkOrderStatus.PENDING_APPROVAL);
        workOrder.setPriority(priority);
        workOrder.setMaintenanceType(MaintenanceType.COMPONENT_OVERHAUL);

        workOrder.setAircraftRegistration("N/A - Component Repair");
        workOrder.setAircraftType("Component");
        workOrder.setEstimatedStartDate(LocalDateTime.now().plusDays(1));
        workOrder.setEstimatedCompletionDate(LocalDateTime.now().plusDays(7));
        workOrder.setEstimatedCost(500.0); // Default cost for manual repair

        workOrder.setAssignedTechnician(technician);
        workOrder.setCreatedBy(currentUser);
        workOrder.setCreatedByUser(username != null ? username : currentUser.getUsername());

        // Ensure parts list initialized and add
        if (workOrder.getRequiredParts() == null) {
            // WorkOrder entity initializes this already, but defensive programming:
            workOrder.setRequiredParts(new java.util.ArrayList<>());
        }
        workOrder.getRequiredParts().add(part);

        WorkOrder saved = workOrderRepository.save(workOrder);
        updatePartStatusForRepair(part);

        log.info("Manual repair work order created: {} for part: {} by user: {}",
                workOrderNumber, part.getPartNumber(), username);

        return saved;
    }
}
