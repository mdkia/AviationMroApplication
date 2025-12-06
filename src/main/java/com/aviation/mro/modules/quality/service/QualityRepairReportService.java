package com.aviation.mro.modules.quality.service;

import com.aviation.mro.modules.quality.repository.InspectionRepository;
import com.aviation.mro.modules.quality.domain.enums.ComplianceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class QualityRepairReportService {

    private final InspectionRepository inspectionRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> getQualityRepairIntegrationReport() {
        Map<String, Object> report = new HashMap<>();

        // Total inspections
        long totalInspections = inspectionRepository.count();
        report.put("totalInspections", totalInspections);

        // Failed inspections
        long failedInspections = inspectionRepository.findByComplianceStatus(ComplianceStatus.NON_COMPLIANT).size();
        report.put("failedInspections", failedInspections);

        // Inspections with repair work orders
        long inspectionsWithRepairOrders = inspectionRepository.findAll().stream()
                .filter(inspection -> inspection.getWorkOrder() != null)
                .count();
        report.put("inspectionsWithRepairOrders", inspectionsWithRepairOrders);

        // Failure rate
        double failureRate = totalInspections > 0 ? (failedInspections * 100.0) / totalInspections : 0.0;
        report.put("failureRate", failureRate);

        // Auto-repair conversion rate
        double autoRepairRate = failedInspections > 0 ? (inspectionsWithRepairOrders * 100.0) / failedInspections : 0.0;
        report.put("autoRepairConversionRate", autoRepairRate);

        return report;
    }
}