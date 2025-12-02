// RepairReportHelper.java
package com.aviation.mro.modules.reporting.service;

import com.aviation.mro.modules.auth.model.User;
import com.aviation.mro.modules.repair.domain.enums.WorkOrderStatus;
import com.aviation.mro.modules.repair.domain.enums.RepairPriority;
import com.aviation.mro.modules.repair.domain.model.WorkOrder;
import com.aviation.mro.modules.repair.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RepairReportHelper {

    private final WorkOrderRepository workOrderRepository;

    public long countInProgressWorkOrders() {
        return workOrderRepository.countByStatus(WorkOrderStatus.IN_PROGRESS);
    }

    public long countOverdueWorkOrders() {
        return workOrderRepository.findOverdueWorkOrders(
                LocalDateTime.now(),
                List.of(WorkOrderStatus.COMPLETED, WorkOrderStatus.CANCELLED)
        ).size();
    }

    public List<Map<String, Object>> getWorkOrderReport(
            LocalDateTime start,
            LocalDateTime end,
            String status) {

        List<WorkOrder> allWorkOrders = workOrderRepository.findAll();

        return allWorkOrders.stream()
                .filter(wo -> start == null ||
                        (wo.getCreatedAt() != null && !wo.getCreatedAt().isBefore(start)))
                .filter(wo -> end == null ||
                        (wo.getCreatedAt() != null && wo.getCreatedAt().isBefore(end)))
                .filter(wo -> status == null ||
                        (wo.getStatus() != null && wo.getStatus().name().equalsIgnoreCase(status.trim())))
                .map(wo -> {
                    boolean isOverdue = wo.getEstimatedCompletionDate() != null &&
                            wo.getEstimatedCompletionDate().isBefore(LocalDateTime.now()) &&
                            wo.getStatus() != WorkOrderStatus.COMPLETED &&
                            wo.getStatus() != WorkOrderStatus.CANCELLED;

                    String technicianName = getFullName(wo.getAssignedTechnician());
                    String createdByName = getFullName(wo.getCreatedBy());

                    return Map.<String, Object>ofEntries(
                            Map.entry("workOrderId", wo.getId()),
                            Map.entry("workOrderNumber", safeString(wo.getWorkOrderNumber())),
                            Map.entry("title", safeString(wo.getTitle())),
                            Map.entry("description", safeString(wo.getDescription())),
                            Map.entry("status", wo.getStatus() != null ? wo.getStatus().name() : "نامشخص"),
                            Map.entry("statusInPersian", getPersianStatus(wo.getStatus())),
                            Map.entry("priority", wo.getPriority() != null ? wo.getPriority().name() : "نامشخص"),
                            Map.entry("priorityInPersian", getPersianPriority(wo.getPriority())),
                            Map.entry("maintenanceType", wo.getMaintenanceType() != null ? wo.getMaintenanceType().name() : ""),
                            Map.entry("aircraftRegistration", safeString(wo.getAircraftRegistration())),
                            Map.entry("estimatedCompletionDate", wo.getEstimatedCompletionDate()),
                            Map.entry("actualCompletionDate", wo.getActualCompletionDate()),
                            Map.entry("estimatedCost", wo.getEstimatedCost() != null ? wo.getEstimatedCost() : 0.0),
                            Map.entry("actualCost", wo.getActualCost() != null ? wo.getActualCost() : 0.0),
                            Map.entry("assignedTechnician", technicianName),
                            Map.entry("createdBy", createdByName),
                            Map.entry("createdAt", wo.getCreatedAt()),
                            Map.entry("isOverdue", isOverdue),
                            Map.entry("tasksCount", wo.getTasks() != null ? wo.getTasks().size() : 0),
                            Map.entry("requiredPartsCount", wo.getRequiredParts() != null ? wo.getRequiredParts().size() : 0)
                    );
                })
                .toList();
    }

    // متد کمکی برای ساختن نام کامل کاربر (چون getFullName() وجود نداره)
    private String getFullName(User user) {
        if (user == null) return "تخصیص نشده";
        String firstName = user.getFirstName() != null ? user.getFirstName().trim() : "";
        String lastName = user.getLastName() != null ? user.getLastName().trim() : "";
        if (firstName.isEmpty() && lastName.isEmpty()) return "نامشخص";
        return (firstName + " " + lastName).trim();
    }

    private String getPersianStatus(WorkOrderStatus status) {
        if (status == null) return "نامشخص";
        return switch (status) {
            case DRAFT -> "پیش‌نویس";
            case APPROVED -> "تأیید شده";
            case IN_PROGRESS -> "در حال انجام";
            case ON_HOLD -> "متوقف شده";
            case COMPLETED -> "تکمیل شده";
            case CANCELLED -> "لغو شده";
            case CLOSED -> "بسته شده";
            default -> status.name();
        };
    }

    private String getPersianPriority(RepairPriority priority) {
        if (priority == null) return "نامشخص";
        return switch (priority) {
            case LOW -> "کم";
            case MEDIUM -> "متوسط";
            case HIGH -> "بالا";
            case CRITICAL -> "بحرانی";
            case AOG -> "AOG - هواپیما زمین‌گیر";
            default -> priority.name();
        };
    }

    private String safeString(String value) {
        return value != null ? value : "";
    }
}