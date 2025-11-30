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
import com.aviation.mro.modules.parts.repository.AircraftPartRepository;
import com.aviation.mro.modules.parts.domain.enums.ServiceabilityStatus;
import com.aviation.mro.modules.parts.domain.enums.LocationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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

        // Get the current user
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

        // Set aircraft information - استفاده از اطلاعات موجود
        workOrder.setAircraftRegistration("N/A - Component Repair");

        // استفاده از partName به جای aircraftType که وجود ندارد
        String aircraftType = inspection.getPart().getPartName() != null ?
                inspection.getPart().getPartName() : "Generic Aircraft Component";
        workOrder.setAircraftType(aircraftType);

        // Set dates
        workOrder.setEstimatedStartDate(LocalDateTime.now().plusDays(1));
        workOrder.setEstimatedCompletionDate(LocalDateTime.now().plusDays(7));
        workOrder.setEstimatedCost(calculateEstimatedRepairCost(inspection));

        // Assign personnel
        workOrder.setAssignedTechnician(assignedTechnician);
        workOrder.setCreatedBy(currentUser);
        workOrder.setCreatedByUser(username);

        // Link to the part that needs repair
        AircraftPart part = inspection.getPart();
        workOrder.getRequiredParts().add(part);

        WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);

        // Update part status to indicate it's in repair
        updatePartStatusForRepair(part);

        log.info("Repair work order created: {} for inspection: {} by user: {}",
                workOrderNumber, inspection.getInspectionNumber(), username);

        return savedWorkOrder;
    }

    private String generateRepairOrderNumber() {
        LocalDateTime now = LocalDateTime.now();
        String baseNumber = "RO-" + now.getYear() + "-" + String.format("%02d", now.getMonthValue());
        long count = workOrderRepository.count() + 1;
        return baseNumber + "-" + String.format("%03d", count);
    }

    private String generateWorkOrderTitle(Inspection inspection) {
        String partInfo = inspection.getPart().getPartNumber() != null ?
                inspection.getPart().getPartNumber() : "Part";
        return "Corrective Repair - " + partInfo + " - " + inspection.getInspectionNumber();
    }

    private String generateWorkOrderDescription(Inspection inspection) {
        StringBuilder description = new StringBuilder();
        description.append("Repair required based on failed inspection: ").append(inspection.getInspectionNumber()).append("\n");

        if (inspection.getFindings() != null) {
            description.append("Findings: ").append(inspection.getFindings()).append("\n");
        }

        if (inspection.getRecommendations() != null) {
            description.append("Recommendations: ").append(inspection.getRecommendations()).append("\n");
        }

        // استفاده از متد getDefectSummary که در کلاس Inspection وجود دارد
        description.append("\nDefect Summary: ").append(inspection.getDefectSummary()).append("\n");

        // Add detailed defect information
        if (inspection.getDefects() != null && !inspection.getDefects().isEmpty()) {
            description.append("\nDetailed Defects:\n");
            inspection.getDefects().stream()
                    .filter(defect -> defect.getComplianceStatus() == ComplianceStatus.NON_COMPLIANT)
                    .forEach(defect -> {
                        description.append("- ").append(defect.getQualityCheck().getDescription());
                        if (defect.getSeverity() != null) {
                            description.append(" (").append(defect.getSeverity()).append(")");
                        }
                        if (defect.getDeviation() != null) {
                            description.append(" - Deviation: ").append(defect.getDeviation());
                        }
                        description.append("\n");
                    });
        }

        return description.toString();
    }

    private RepairPriority determineRepairPriority(Inspection inspection) {
        // استفاده از متدهای جدید hasCriticalDefects و hasMajorDefects که در Inspection وجود دارند
        if (inspection.hasCriticalDefects()) {
            return RepairPriority.CRITICAL;
        } else if (inspection.hasMajorDefects()) {
            return RepairPriority.HIGH;
        } else {
            return RepairPriority.MEDIUM;
        }
    }

    private MaintenanceType determineMaintenanceType(Inspection inspection) {
        // منطق ساده برای تعیین نوع تعمیر بر اساس قطعه و نقص‌ها
        if (inspection.getPart().getPartNumber() != null &&
                inspection.getPart().getPartNumber().toUpperCase().contains("ENG")) {
            return MaintenanceType.ENGINE_REPAIR;
        } else if (inspection.getDefects() != null &&
                inspection.getDefects().stream().anyMatch(defect ->
                        defect.getQualityCheck().getDescription().toLowerCase().contains("structural"))) {
            return MaintenanceType.STRUCTURAL_REPAIR;
        } else {
            return MaintenanceType.COMPONENT_OVERHAUL;
        }
    }

    private Double calculateEstimatedRepairCost(Inspection inspection) {
        // محاسبه هزینه پایه
        double baseCost = 500.0; // هزینه پایه پیش‌فرض

        // تنظیم بر اساس شدت نقص
        if (inspection.getDefects() != null) {
            long criticalDefects = inspection.getDefects().stream()
                    .filter(defect -> defect.getSeverity() == DefectSeverity.CRITICAL)
                    .count();

            long majorDefects = inspection.getDefects().stream()
                    .filter(defect -> defect.getSeverity() == DefectSeverity.MAJOR)
                    .count();

            // ضرایب هزینه
            baseCost += (criticalDefects * 1000.0); // 1000 دلار برای هر نقص بحرانی
            baseCost += (majorDefects * 500.0);     // 500 دلار برای هر نقص اصلی
        }

        // در مدل AircraftPart فیلد هزینه وجود ندارد، بنابراین از هزینه ثابت استفاده می‌کنیم
        // اگر نیاز به هزینه بود، می‌توان از منطق جایگزین استفاده کرد
        baseCost = Math.max(baseCost, 300.0); // حداقل هزینه 300 دلار

        return baseCost;
    }

    private User findAvailableTechnician() {
        // دریافت تمام کاربران
        List<User> allUsers = userRepository.findAll();

        // پیدا کردن کاربران با نقش TECHNICIAN
        return allUsers.stream()
                .filter(user -> {
                    // بررسی وضعیت فعال بودن کاربر - استفاده از isEnabled()
                    return user.isEnabled() && !user.isDeleted();
                })
                .filter(user -> {
                    // بررسی اینکه کاربر نقش TECHNICIAN دارد
                    // با توجه به ساختار واقعی مدل User - نقش‌ها به صورت String هستند
                    Set<String> roles = user.getRoles();
                    if (roles == null || roles.isEmpty()) {
                        return false;
                    }

                    // بررسی بر اساس نام نقش (String)
                    return roles.stream()
                            .anyMatch(role -> "TECHNICIAN".equalsIgnoreCase(role));
                })
                .findFirst()
                .orElse(null); // برگرداندن null اگر تکنسینی پیدا نشد
    }

    private void updatePartStatusForRepair(AircraftPart part) {
        // به‌روزرسانی وضعیت قطعه برای نشان دادن اینکه در تعمیر است
        part.setServiceabilityStatus(ServiceabilityStatus.UNSERVICEABLE_REPAIRABLE);
        part.setLocationStatus(LocationStatus.IN_REPAIR_SHOP);
        aircraftPartRepository.save(part);

        log.debug("Part status updated for repair: {}", part.getPartNumber());
    }

    // متد اضافی برای ایجاد دستی سفارش کار تعمیر
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
        workOrder.setAircraftType(part.getPartName() != null ? part.getPartName() : "Generic Aircraft Component");
        workOrder.setEstimatedStartDate(LocalDateTime.now().plusDays(1));
        workOrder.setEstimatedCompletionDate(LocalDateTime.now().plusDays(7));
        workOrder.setEstimatedCost(500.0); // هزینه پیش‌فرض برای تعمیر دستی
        workOrder.setAssignedTechnician(technician);
        workOrder.setCreatedBy(currentUser);
        workOrder.setCreatedByUser(username);
        workOrder.getRequiredParts().add(part);

        WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);
        updatePartStatusForRepair(part);

        log.info("Manual repair work order created: {} for part: {} by user: {}",
                workOrderNumber, part.getPartNumber(), username);

        return savedWorkOrder;
    }

    // متد کمکی برای بررسی در دسترس بودن تکنسین‌ها
    public boolean hasAvailableTechnicians() {
        return findAvailableTechnician() != null;
    }

    // متد برای دریافت لیست تمام تکنسین‌های موجود
    public List<User> getAllAvailableTechnicians() {
        List<User> allUsers = userRepository.findAll();

        return allUsers.stream()
                .filter(user -> user.isEnabled() && !user.isDeleted())
                .filter(user -> {
                    Set<String> roles = user.getRoles();
                    if (roles == null || roles.isEmpty()) {
                        return false;
                    }
                    return roles.stream()
                            .anyMatch(role -> "TECHNICIAN".equalsIgnoreCase(role));
                })
                .toList();
    }

    // متد برای بررسی اینکه آیا بازرسی نیاز به تعمیر دارد
    public boolean inspectionRequiresRepair(Inspection inspection) {
        return inspection != null &&
                inspection.requiresRepair() &&
                inspection.getPart() != null;
    }
}