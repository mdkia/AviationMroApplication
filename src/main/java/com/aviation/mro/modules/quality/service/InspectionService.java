package com.aviation.mro.modules.quality.service;
//
//import com.aviation.mro.modules.quality.domain.dto.*;
//import com.aviation.mro.modules.quality.domain.model.*;
//import com.aviation.mro.modules.quality.domain.enums.InspectionStatus;
//import com.aviation.mro.modules.quality.domain.enums.ComplianceStatus;
//import com.aviation.mro.modules.quality.repository.*;
//import com.aviation.mro.modules.auth.model.User;
//import com.aviation.mro.modules.auth.repository.UserRepository;
//import com.aviation.mro.modules.parts.domain.model.AircraftPart;
//import com.aviation.mro.modules.parts.repository.AircraftPartRepository;
//import com.aviation.mro.modules.repair.domain.model.WorkOrder;
//import com.aviation.mro.modules.repair.repository.WorkOrderRepository;
//import com.aviation.mro.shared.exceptions.NotFoundException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class InspectionService {
//
//    private final InspectionRepository inspectionRepository;
//    private final InspectionPlanRepository inspectionPlanRepository;
//    private final AircraftPartRepository aircraftPartRepository;
//    private final WorkOrderRepository workOrderRepository;
//    private final UserRepository userRepository;
//    private final DefectRepository defectRepository;
//    private final QualityCheckRepository qualityCheckRepository;
//
//    @Transactional
//    public InspectionResponse createInspection(InspectionRequest request, String username) {
//        InspectionPlan inspectionPlan = inspectionPlanRepository.findById(request.getInspectionPlanId())
//                .orElseThrow(() -> new NotFoundException("Inspection plan not found with id: " + request.getInspectionPlanId()));
//
//        // Generate inspection number
//        String inspectionNumber = generateInspectionNumber();
//
//        Inspection inspection = new Inspection();
//        inspection.setInspectionNumber(inspectionNumber);
//        inspection.setInspectionPlan(inspectionPlan);
//        inspection.setStatus(InspectionStatus.IN_PROGRESS);
//        inspection.setScheduledDate(request.getScheduledDate());
//        inspection.setActualStartDate(LocalDateTime.now());
//        inspection.setCreatedBy(username);
//
//        // Set part if provided
//        if (request.getPartId() != null) {
//            AircraftPart part = aircraftPartRepository.findById(request.getPartId())
//                    .orElseThrow(() -> new NotFoundException("Part not found with id: " + request.getPartId()));
//            inspection.setPart(part);
//        }
//
//        // Set work order if provided
//        if (request.getWorkOrderId() != null) {
//            WorkOrder workOrder = workOrderRepository.findById(request.getWorkOrderId())
//                    .orElseThrow(() -> new NotFoundException("Work order not found with id: " + request.getWorkOrderId()));
//            inspection.setWorkOrder(workOrder);
//        }
//
//        // Set inspector if provided
//        if (request.getInspectorId() != null) {
//            User inspector = userRepository.findById(request.getInspectorId())
//                    .orElseThrow(() -> new NotFoundException("Inspector not found with id: " + request.getInspectorId()));
//            inspection.setInspector(inspector);
//        }
//
//        // Add defects
//        for (DefectRequest defectRequest : request.getDefects()) {
//            QualityCheck qualityCheck = qualityCheckRepository.findById(defectRequest.getQualityCheckId())
//                    .orElseThrow(() -> new NotFoundException("Quality check not found with id: " + defectRequest.getQualityCheckId()));
//
//            Defect defect = new Defect();
//            defect.setQualityCheck(qualityCheck);
//            defect.setComplianceStatus(defectRequest.getComplianceStatus());
//            defect.setSeverity(defectRequest.getSeverity());
//            defect.setActualValue(defectRequest.getActualValue());
//            defect.setDeviation(defectRequest.getDeviation());
//            defect.setNotes(defectRequest.getNotes());
//            defect.setEvidencePhotos(defectRequest.getEvidencePhotos());
//
//            inspection.getDefects().add(defect);
//            defect.setInspection(inspection);
//        }
//
//        // Calculate inspection results
//        inspection.calculateResults();
//
//        Inspection savedInspection = inspectionRepository.save(inspection);
//        log.info("Inspection created: {} by user: {}", inspectionNumber, username);
//
//        return mapToInspectionResponse(savedInspection);
//    }
//
//    @Transactional
//    public InspectionResponse completeInspection(Long inspectionId, String findings, String recommendations, String username) {
//        Inspection inspection = inspectionRepository.findById(inspectionId)
//                .orElseThrow(() -> new NotFoundException("Inspection not found with id: " + inspectionId));
//
//        inspection.setStatus(InspectionStatus.COMPLETED);
//        inspection.setActualEndDate(LocalDateTime.now());
//        inspection.setFindings(findings);
//        inspection.setRecommendations(recommendations);
//        inspection.setUpdatedBy(username);
//
//        // Integration with Parts Module : Update part status based on inspection results if part is associated
//        if (inspection.getPart() != null && inspection.getComplianceStatus() == ComplianceStatus.NON_COMPLIANT) {
//            AircraftPart part = inspection.getPart();
//            part.setServiceabilityStatus(com.aviation.mro.modules.parts.domain.enums.ServiceabilityStatus.UNSERVICEABLE_REPAIRABLE);
//            part.setLocationStatus(com.aviation.mro.modules.parts.domain.enums.LocationStatus.QUARANTINED_AREA);
//            aircraftPartRepository.save(part);
//        }
//
//        Inspection completedInspection = inspectionRepository.save(inspection);
//        log.info("Inspection completed: {} by user: {}", inspection.getInspectionNumber(), username);
//
//        return mapToInspectionResponse(completedInspection);
//    }
//
//    @Transactional(readOnly = true)
//    public List<InspectionResponse> getAllInspections() {
//        return inspectionRepository.findAll().stream()
//                .map(this::mapToInspectionResponse)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional(readOnly = true)
//    public InspectionResponse getInspectionById(Long id) {
//        Inspection inspection = inspectionRepository.findById(id)
//                .orElseThrow(() -> new NotFoundException("Inspection not found with id: " + id));
//        return mapToInspectionResponse(inspection);
//    }
//
//    @Transactional(readOnly = true)
//    public InspectionResponse getInspectionByNumber(String inspectionNumber) {
//        Inspection inspection = inspectionRepository.findByInspectionNumber(inspectionNumber)
//                .orElseThrow(() -> new NotFoundException("Inspection not found: " + inspectionNumber));
//        return mapToInspectionResponse(inspection);
//    }
//
//    @Transactional(readOnly = true)
//    public List<InspectionResponse> getInspectionsByStatus(InspectionStatus status) {
//        return inspectionRepository.findByStatus(status).stream()
//                .map(this::mapToInspectionResponse)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional(readOnly = true)
//    public List<InspectionResponse> getInspectionsByPart(Long partId) {
//        return inspectionRepository.findByPartId(partId).stream()
//                .map(this::mapToInspectionResponse)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional(readOnly = true)
//    public List<InspectionResponse> getInspectionsByWorkOrder(Long workOrderId) {
//        return inspectionRepository.findByWorkOrderId(workOrderId).stream()
//                .map(this::mapToInspectionResponse)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional(readOnly = true)
//    public List<InspectionResponse> getInProgressInspections() {
//        return inspectionRepository.findInProgressInspections().stream()
//                .map(this::mapToInspectionResponse)
//                .collect(Collectors.toList());
//    }
//
//    // Helper method to generate inspection number
//    private String generateInspectionNumber() {
//        long count = inspectionRepository.count() + 1;
//        return "INS-" + LocalDateTime.now().getYear() + "-" + String.format("%03d", count);
//    }
//
//    // Mapping helper
//    private InspectionResponse mapToInspectionResponse(Inspection inspection) {
//        InspectionResponse response = new InspectionResponse();
//        response.setId(inspection.getId());
//        response.setInspectionNumber(inspection.getInspectionNumber());
//        response.setInspectionPlanId(inspection.getInspectionPlan().getId());
//        response.setInspectionPlanTitle(inspection.getInspectionPlan().getTitle());
//        response.setStatus(inspection.getStatus());
//        response.setComplianceStatus(inspection.getComplianceStatus());
//        response.setScheduledDate(inspection.getScheduledDate());
//        response.setActualStartDate(inspection.getActualStartDate());
//        response.setActualEndDate(inspection.getActualEndDate());
//        response.setTotalChecks(inspection.getTotalChecks());
//        response.setPassedChecks(inspection.getPassedChecks());
//        response.setFailedChecks(inspection.getFailedChecks());
//        response.setComplianceRate(inspection.getComplianceRate());
//        response.setFindings(inspection.getFindings());
//        response.setRecommendations(inspection.getRecommendations());
//        response.setCreatedAt(inspection.getCreatedAt());
//        response.setUpdatedAt(inspection.getUpdatedAt());
//
//        // Map part info
//        if (inspection.getPart() != null) {
//            response.setPartId(inspection.getPart().getId());
//            response.setPartNumber(inspection.getPart().getPartNumber());
//        }
//
//        // Map work order info
//        if (inspection.getWorkOrder() != null) {
//            response.setWorkOrderId(inspection.getWorkOrder().getId());
//            response.setWorkOrderNumber(inspection.getWorkOrder().getWorkOrderNumber());
//        }
//
//        // Map inspector info
//        if (inspection.getInspector() != null) {
//            response.setInspectorId(inspection.getInspector().getId());
//            response.setInspectorName(inspection.getInspector().getFirstName() + " " + inspection.getInspector().getLastName());
//        }
//
//        // Map defects
//        if (inspection.getDefects() != null) {
//            List<InspectionResponse.DefectResponse> defectResponses = inspection.getDefects().stream()
//                    .map(defect -> {
//                        InspectionResponse.DefectResponse defectResponse = new InspectionResponse.DefectResponse();
//                        defectResponse.setId(defect.getId());
//                        defectResponse.setQualityCheckId(defect.getQualityCheck().getId());
//                        defectResponse.setQualityCheckCode(defect.getQualityCheck().getCheckCode());
//                        defectResponse.setQualityCheckDescription(defect.getQualityCheck().getDescription());
//                        defectResponse.setComplianceStatus(defect.getComplianceStatus());
//                        defectResponse.setSeverity(defect.getSeverity());
//                        defectResponse.setActualValue(defect.getActualValue());
//                        defectResponse.setDeviation(defect.getDeviation());
//                        defectResponse.setNotes(defect.getNotes());
//                        defectResponse.setEvidencePhotos(defect.getEvidencePhotos());
//                        return defectResponse;
//                    })
//                    .collect(Collectors.toList());
//            response.setDefects(defectResponses);
//        }
//
//        // Map NCR summary
//        if (inspection.getNonConformanceReport() != null) {
//            InspectionResponse.NCRSummary ncrSummary = new InspectionResponse.NCRSummary();
//            ncrSummary.setNcrId(inspection.getNonConformanceReport().getId());
//            ncrSummary.setNcrNumber(inspection.getNonConformanceReport().getNcrNumber());
//            ncrSummary.setStatus(inspection.getNonConformanceReport().getStatus().toString());
//            response.setNcrSummary(ncrSummary);
//        }
//
//        return response;
//    }
//}

import com.aviation.mro.modules.quality.domain.dto.*;
import com.aviation.mro.modules.quality.domain.model.*;
import com.aviation.mro.modules.quality.domain.enums.InspectionStatus;
import com.aviation.mro.modules.quality.domain.enums.ComplianceStatus;
import com.aviation.mro.modules.quality.repository.*;
import com.aviation.mro.modules.auth.model.User;
import com.aviation.mro.modules.auth.repository.UserRepository;
import com.aviation.mro.modules.parts.domain.model.AircraftPart;
import com.aviation.mro.modules.parts.repository.AircraftPartRepository;
import com.aviation.mro.modules.repair.domain.model.WorkOrder;
import com.aviation.mro.modules.repair.repository.WorkOrderRepository;
import com.aviation.mro.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionService {

    private final InspectionRepository inspectionRepository;
    private final InspectionPlanRepository inspectionPlanRepository;
    private final AircraftPartRepository aircraftPartRepository;
    private final WorkOrderRepository workOrderRepository;
    private final UserRepository userRepository;
    private final DefectRepository defectRepository;
    private final QualityCheckRepository qualityCheckRepository;
    private final RepairIntegrationService repairIntegrationService;

    @Transactional
    public InspectionResponse createInspection(InspectionRequest request, String username) {
        InspectionPlan inspectionPlan = inspectionPlanRepository.findById(request.getInspectionPlanId())
                .orElseThrow(() -> new NotFoundException("Inspection plan not found with id: " + request.getInspectionPlanId()));

        // Generate inspection number
        String inspectionNumber = generateInspectionNumber();

        Inspection inspection = new Inspection();
        inspection.setInspectionNumber(inspectionNumber);
        inspection.setInspectionPlan(inspectionPlan);
        inspection.setStatus(InspectionStatus.IN_PROGRESS);
        inspection.setScheduledDate(request.getScheduledDate());
        inspection.setActualStartDate(LocalDateTime.now());
        inspection.setCreatedBy(username);

        // Set part if provided
        if (request.getPartId() != null) {
            AircraftPart part = aircraftPartRepository.findById(request.getPartId())
                    .orElseThrow(() -> new NotFoundException("Part not found with id: " + request.getPartId()));
            inspection.setPart(part);
        }

        // Set work order if provided
        if (request.getWorkOrderId() != null) {
            WorkOrder workOrder = workOrderRepository.findById(request.getWorkOrderId())
                    .orElseThrow(() -> new NotFoundException("Work order not found with id: " + request.getWorkOrderId()));
            inspection.setWorkOrder(workOrder);
        }

        // Set inspector if provided
        if (request.getInspectorId() != null) {
            User inspector = userRepository.findById(request.getInspectorId())
                    .orElseThrow(() -> new NotFoundException("Inspector not found with id: " + request.getInspectorId()));
            inspection.setInspector(inspector);
        }

        // Add defects
        for (DefectRequest defectRequest : request.getDefects()) {
            QualityCheck qualityCheck = qualityCheckRepository.findById(defectRequest.getQualityCheckId())
                    .orElseThrow(() -> new NotFoundException("Quality check not found with id: " + defectRequest.getQualityCheckId()));

            Defect defect = new Defect();
            defect.setQualityCheck(qualityCheck);
            defect.setComplianceStatus(defectRequest.getComplianceStatus());
            defect.setSeverity(defectRequest.getSeverity());
            defect.setActualValue(defectRequest.getActualValue());
            defect.setDeviation(defectRequest.getDeviation());
            defect.setNotes(defectRequest.getNotes());
            defect.setEvidencePhotos(defectRequest.getEvidencePhotos());

            inspection.getDefects().add(defect);
            defect.setInspection(inspection);
        }

        // Calculate inspection results
        inspection.calculateResults();

        Inspection savedInspection = inspectionRepository.save(inspection);
        log.info("Inspection created: {} by user: {}", inspectionNumber, username);

        return mapToInspectionResponse(savedInspection);
    }

    @Transactional
    public InspectionResponse completeInspection(Long inspectionId, String findings, String recommendations, String username) {
        Inspection inspection = inspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new NotFoundException("Inspection not found with id: " + inspectionId));

        inspection.setStatus(InspectionStatus.COMPLETED);
        inspection.setActualEndDate(LocalDateTime.now());
        inspection.setFindings(findings);
        inspection.setRecommendations(recommendations);
        inspection.setUpdatedBy(username);

        // Calculate results
        inspection.calculateResults();

        // 🔄 INTEGRATION: Create repair work order if inspection failed
        if (inspection.requiresRepair()) {
            try {
                WorkOrder repairWorkOrder = repairIntegrationService.createRepairWorkOrderFromInspection(inspection, username);
                log.info("Automatically created repair work order: {} for failed inspection: {}",
                        repairWorkOrder.getWorkOrderNumber(), inspection.getInspectionNumber());

                // Link the work order to inspection
                inspection.setWorkOrder(repairWorkOrder);
            } catch (Exception e) {
                log.error("Failed to create automatic repair work order for inspection: {}",
                        inspection.getInspectionNumber(), e);
                // Don't fail the inspection completion if repair order creation fails
                // Just log the error and continue
            }
        }

        // Update part status based on inspection results
        updatePartStatusAfterInspection(inspection);

        Inspection completedInspection = inspectionRepository.save(inspection);
        log.info("Inspection completed: {} by user: {}", inspection.getInspectionNumber(), username);

        return mapToInspectionResponse(completedInspection);
    }

//    @Transactional
//    public InspectionResponse completeInspection(Long inspectionId, String findings, String recommendations, String username) {
//        Inspection inspection = inspectionRepository.findById(inspectionId)
//                .orElseThrow(() -> new NotFoundException("Inspection not found with id: " + inspectionId));
//
//        inspection.setStatus(InspectionStatus.COMPLETED);
//        inspection.setActualEndDate(LocalDateTime.now());
//        inspection.setFindings(findings);
//        inspection.setRecommendations(recommendations);
//        inspection.setUpdatedBy(username);
//
//        // Calculate results
//        inspection.calculateResults();
//
//        // 🔄 INTEGRATION: Create repair work order if inspection failed
//        if (inspection.requiresRepair()) {
//            try {
//                WorkOrder repairWorkOrder = repairIntegrationService.createRepairWorkOrderFromInspection(inspection, username);
//                log.info("Automatically created repair work order: {} for failed inspection: {}",
//                        repairWorkOrder.getWorkOrderNumber(), inspection.getInspectionNumber());
//
//                // Link the work order to inspection
//                inspection.setWorkOrder(repairWorkOrder);
//            } catch (Exception e) {
//                log.error("Failed to create automatic repair work order for inspection: {}",
//                        inspection.getInspectionNumber(), e);
//                // Don't fail the inspection completion if repair order creation fails
//                // Just log the error and continue
//            }
//        }
//
//        // Update part status based on inspection results
//        updatePartStatusAfterInspection(inspection);
//
//        Inspection completedInspection = inspectionRepository.save(inspection);
//        log.info("Inspection completed: {} by user: {}", inspection.getInspectionNumber(), username);
//
//        return mapToInspectionResponse(completedInspection);
//    }

    private void updatePartStatusAfterInspection(Inspection inspection) {
        if (inspection.getPart() != null) {
            AircraftPart part = inspection.getPart();

            switch (inspection.getComplianceStatus()) {
                case COMPLIANT:
                    part.setServiceabilityStatus(com.aviation.mro.modules.parts.domain.enums.ServiceabilityStatus.SERVICEABLE);
                    part.setLocationStatus(com.aviation.mro.modules.parts.domain.enums.LocationStatus.IN_STOCK);
                    break;
                case NON_COMPLIANT:
                    part.setServiceabilityStatus(com.aviation.mro.modules.parts.domain.enums.ServiceabilityStatus.UNSERVICEABLE_REPAIRABLE);
                    part.setLocationStatus(com.aviation.mro.modules.parts.domain.enums.LocationStatus.IN_REPAIR_SHOP);
                    break;
                case CONDITIONAL_APPROVAL:
                    part.setServiceabilityStatus(com.aviation.mro.modules.parts.domain.enums.ServiceabilityStatus.SERVICEABLE);
                    part.setLocationStatus(com.aviation.mro.modules.parts.domain.enums.LocationStatus.IN_STOCK);
                    break;
            }

            aircraftPartRepository.save(part);
            log.debug("Part status updated after inspection: {} - {}",
                    part.getPartNumber(), inspection.getComplianceStatus());
        }
    }

    @Transactional(readOnly = true)
    public List<InspectionResponse> getAllInspections() {
        return inspectionRepository.findAll().stream()
                .map(this::mapToInspectionResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InspectionResponse getInspectionById(Long id) {
        Inspection inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Inspection not found with id: " + id));
        return mapToInspectionResponse(inspection);
    }

    @Transactional(readOnly = true)
    public InspectionResponse getInspectionByNumber(String inspectionNumber) {
        Inspection inspection = inspectionRepository.findByInspectionNumber(inspectionNumber)
                .orElseThrow(() -> new NotFoundException("Inspection not found: " + inspectionNumber));
        return mapToInspectionResponse(inspection);
    }

    @Transactional(readOnly = true)
    public List<InspectionResponse> getInspectionsByStatus(InspectionStatus status) {
        return inspectionRepository.findByStatus(status).stream()
                .map(this::mapToInspectionResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InspectionResponse> getInspectionsByPart(Long partId) {
        return inspectionRepository.findByPartId(partId).stream()
                .map(this::mapToInspectionResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InspectionResponse> getInspectionsByWorkOrder(Long workOrderId) {
        return inspectionRepository.findByWorkOrderId(workOrderId).stream()
                .map(this::mapToInspectionResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InspectionResponse> getInProgressInspections() {
        return inspectionRepository.findInProgressInspections().stream()
                .map(this::mapToInspectionResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public WorkOrder createManualRepairOrderFromInspection(Long inspectionId, String username) {
        Inspection inspection = inspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new NotFoundException("Inspection not found with id: " + inspectionId));

        // استفاده از متد جدید requiresRepair
        if (!inspection.requiresRepair()) {
            throw new IllegalStateException("Inspection does not require repair - Compliance status: " +
                    inspection.getComplianceStatus() + ", Part: " +
                    (inspection.getPart() != null ? "present" : "missing"));
        }

        WorkOrder workOrder = repairIntegrationService.createRepairWorkOrderFromInspection(inspection, username);

        // Link the work order to inspection
        inspection.setWorkOrder(workOrder);
        inspectionRepository.save(inspection);

        log.info("Manual repair work order created from inspection: {} by user: {}",
                inspection.getInspectionNumber(), username);

        return workOrder;
    }

//    @Transactional
//    public WorkOrder createManualRepairOrderFromInspection(Long inspectionId, String username) {
//        Inspection inspection = inspectionRepository.findById(inspectionId)
//                .orElseThrow(() -> new NotFoundException("Inspection not found with id: " + inspectionId));
//
//        if (!inspection.requiresRepair()) {
//            throw new IllegalStateException("Inspection does not require repair");
//        }
//
//        WorkOrder workOrder = repairIntegrationService.createRepairWorkOrderFromInspection(inspection, username);
//
//        // Link the work order to inspection
//        inspection.setWorkOrder(workOrder);
//        inspectionRepository.save(inspection);
//
//        log.info("Manual repair work order created from inspection: {} by user: {}",
//                inspection.getInspectionNumber(), username);
//
//        return workOrder;
//    }

    // Helper method to generate inspection number
    private String generateInspectionNumber() {
        long count = inspectionRepository.count() + 1;
        return "INS-" + LocalDateTime.now().getYear() + "-" + String.format("%03d", count);
    }

    // Mapping helper (بقیه متد بدون تغییر)
    private InspectionResponse mapToInspectionResponse(Inspection inspection) {
        // ... existing mapping code (بدون تغییر) ...
        InspectionResponse response = new InspectionResponse();
        response.setId(inspection.getId());
        response.setInspectionNumber(inspection.getInspectionNumber());
        response.setInspectionPlanId(inspection.getInspectionPlan().getId());
        response.setInspectionPlanTitle(inspection.getInspectionPlan().getTitle());
        response.setStatus(inspection.getStatus());
        response.setComplianceStatus(inspection.getComplianceStatus());
        response.setScheduledDate(inspection.getScheduledDate());
        response.setActualStartDate(inspection.getActualStartDate());
        response.setActualEndDate(inspection.getActualEndDate());
        response.setTotalChecks(inspection.getTotalChecks());
        response.setPassedChecks(inspection.getPassedChecks());
        response.setFailedChecks(inspection.getFailedChecks());
        response.setComplianceRate(inspection.getComplianceRate());
        response.setFindings(inspection.getFindings());
        response.setRecommendations(inspection.getRecommendations());
        response.setCreatedAt(inspection.getCreatedAt());
        response.setUpdatedAt(inspection.getUpdatedAt());

        // Map part info
        if (inspection.getPart() != null) {
            response.setPartId(inspection.getPart().getId());
            response.setPartNumber(inspection.getPart().getPartNumber());
        }

        // Map work order info
        if (inspection.getWorkOrder() != null) {
            response.setWorkOrderId(inspection.getWorkOrder().getId());
            response.setWorkOrderNumber(inspection.getWorkOrder().getWorkOrderNumber());
        }

        // Map inspector info
        if (inspection.getInspector() != null) {
            response.setInspectorId(inspection.getInspector().getId());
            response.setInspectorName(inspection.getInspector().getFirstName() + " " + inspection.getInspector().getLastName());
        }

        // Map defects
        if (inspection.getDefects() != null) {
            List<InspectionResponse.DefectResponse> defectResponses = inspection.getDefects().stream()
                    .map(defect -> {
                        InspectionResponse.DefectResponse defectResponse = new InspectionResponse.DefectResponse();
                        defectResponse.setId(defect.getId());
                        defectResponse.setQualityCheckId(defect.getQualityCheck().getId());
                        defectResponse.setQualityCheckCode(defect.getQualityCheck().getCheckCode());
                        defectResponse.setQualityCheckDescription(defect.getQualityCheck().getDescription());
                        defectResponse.setComplianceStatus(defect.getComplianceStatus());
                        defectResponse.setSeverity(defect.getSeverity());
                        defectResponse.setActualValue(defect.getActualValue());
                        defectResponse.setDeviation(defect.getDeviation());
                        defectResponse.setNotes(defect.getNotes());
                        defectResponse.setEvidencePhotos(defect.getEvidencePhotos());
                        return defectResponse;
                    })
                    .collect(Collectors.toList());
            response.setDefects(defectResponses);
        }

        // Map NCR summary
        if (inspection.getNonConformanceReport() != null) {
            InspectionResponse.NCRSummary ncrSummary = new InspectionResponse.NCRSummary();
            ncrSummary.setNcrId(inspection.getNonConformanceReport().getId());
            ncrSummary.setNcrNumber(inspection.getNonConformanceReport().getNcrNumber());
            ncrSummary.setStatus(inspection.getNonConformanceReport().getStatus().toString());
            response.setNcrSummary(ncrSummary);
        }

        return response;
    }
}