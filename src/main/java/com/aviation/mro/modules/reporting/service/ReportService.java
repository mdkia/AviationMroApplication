package com.aviation.mro.modules.reporting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final InventoryReportHelper inventoryHelper;
    private final QualityReportHelper qualityHelper;
    private final RepairReportHelper repairHelper;
    private final AccountingReportHelper accountingHelper;
    private final SalesReportHelper salesHelper;

    public Map<String, Object> getDashboardSummary() {
        return Map.ofEntries(
                Map.entry("totalParts", inventoryHelper.countTotalParts()),
                Map.entry("lowStockCount", inventoryHelper.countLowStock()),
                Map.entry("criticalStockCount", inventoryHelper.countCriticalStock()),

                // به جای pendingInspections از چیزی که داریم استفاده می‌کنیم
                Map.entry("openNonConformances", qualityHelper.countOpenNonConformances()),
                Map.entry("overdueNonConformances", qualityHelper.countOverdueNonConformances()),

                Map.entry("inProgressWorkOrders", repairHelper.countInProgressWorkOrders()),
                Map.entry("overdueWorkOrders", repairHelper.countOverdueWorkOrders()),

                Map.entry("totalRevenueThisMonth", accountingHelper.getRevenueThisMonth()),
                Map.entry("unpaidInvoices", salesHelper.countUnpaidInvoices()),

                Map.entry("generatedAt", LocalDateTime.now())
        );
    }

    // بقیه متدها بدون تغییر...
    public List<?> getInventoryStatusReport(String partNumber, String status) {
        return inventoryHelper.getInventoryReport(partNumber, status);
    }

    public List<?> getNonConformanceReport(LocalDateTime start, LocalDateTime end, String severity, String status) {
        return qualityHelper.getNonConformanceReport(start, end, severity, status);
    }

    public List<?> getWorkOrderReport(LocalDateTime start, LocalDateTime end, String status) {
        return repairHelper.getWorkOrderReport(start, end, status);
    }

    public Map<String, Object> getFinancialSummary(LocalDateTime start, LocalDateTime end) {
        return accountingHelper.getFinancialSummary(start, end);
    }

    public Map<String, Object> getSalesPerformance(LocalDateTime start, LocalDateTime end) {
        return salesHelper.getSalesPerformance(start, end);
    }
}