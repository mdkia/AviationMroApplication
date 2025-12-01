// modules/quality/service/InspectionPlanService.java
package com.aviation.mro.modules.quality.service;

import com.aviation.mro.modules.quality.domain.dto.*;
import com.aviation.mro.modules.quality.domain.model.*;
import com.aviation.mro.modules.quality.repository.*;
import com.aviation.mro.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionPlanService {

    private final InspectionPlanRepository inspectionPlanRepository;
    private final QualityStandardRepository qualityStandardRepository;
    private final QualityCheckRepository qualityCheckRepository;

    @Transactional
    public InspectionPlanResponse createInspectionPlan(InspectionPlanRequest request, String username) {
        // Generate plan number
        String planNumber = generatePlanNumber();

        InspectionPlan inspectionPlan = new InspectionPlan();
        inspectionPlan.setPlanNumber(planNumber);
        inspectionPlan.setTitle(request.getTitle());
        inspectionPlan.setDescription(request.getDescription());
        inspectionPlan.setInspectionType(request.getInspectionType());
        inspectionPlan.setApplicableStandards(request.getApplicableStandards());
        inspectionPlan.setInspectionFrequencyDays(request.getInspectionFrequencyDays());
        inspectionPlan.setSampleSize(request.getSampleSize());
        inspectionPlan.setCreatedBy(username);

        // Set quality standard if provided
        if (request.getQualityStandardId() != null) {
            QualityStandard qualityStandard = qualityStandardRepository.findById(request.getQualityStandardId())
                    .orElseThrow(() -> new NotFoundException("Quality standard not found with id: " + request.getQualityStandardId()));
            inspectionPlan.setQualityStandard(qualityStandard);
        }

        // Add quality checkpoints
        for (QualityCheckRequest checkpointRequest : request.getCheckpoints()) {
            QualityCheck checkpoint = new QualityCheck();
            checkpoint.setCheckCode(checkpointRequest.getCheckCode());
            checkpoint.setDescription(checkpointRequest.getDescription());
            checkpoint.setAcceptanceCriteria(checkpointRequest.getAcceptanceCriteria());
            checkpoint.setMeasurementMethod(checkpointRequest.getMeasurementMethod());
            checkpoint.setToolsRequired(checkpointRequest.getToolsRequired());
            checkpoint.setIsCritical(checkpointRequest.getIsCritical());
            checkpoint.setSequenceNumber(checkpointRequest.getSequenceNumber());

            inspectionPlan.addCheckpoint(checkpoint);
        }

        InspectionPlan savedPlan = inspectionPlanRepository.save(inspectionPlan);
        log.info("Inspection plan created: {} by user: {}", planNumber, username);

        return mapToInspectionPlanResponse(savedPlan);
    }

    @Transactional(readOnly = true)
    public List<InspectionPlanResponse> getAllInspectionPlans() {
        return inspectionPlanRepository.findAll().stream()
                .map(this::mapToInspectionPlanResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InspectionPlanResponse getInspectionPlanById(Long id) {
        InspectionPlan inspectionPlan = inspectionPlanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Inspection plan not found with id: " + id));
        return mapToInspectionPlanResponse(inspectionPlan);
    }

    @Transactional(readOnly = true)
    public InspectionPlanResponse getInspectionPlanByNumber(String planNumber) {
        InspectionPlan inspectionPlan = inspectionPlanRepository.findByPlanNumber(planNumber)
                .orElseThrow(() -> new NotFoundException("Inspection plan not found: " + planNumber));
        return mapToInspectionPlanResponse(inspectionPlan);
    }

    @Transactional(readOnly = true)
    public List<InspectionPlanResponse> getActiveInspectionPlans() {
        return inspectionPlanRepository.findByIsActiveTrue().stream()
                .map(this::mapToInspectionPlanResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public InspectionPlanResponse updatePlanStatus(Long id, Boolean isActive, String username) {
        InspectionPlan inspectionPlan = inspectionPlanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Inspection plan not found with id: " + id));

        inspectionPlan.setIsActive(isActive);
        inspectionPlan.setUpdatedBy(username);

        InspectionPlan updatedPlan = inspectionPlanRepository.save(inspectionPlan);
        log.info("Inspection plan status updated: {} to {} by user: {}",
                inspectionPlan.getPlanNumber(), isActive, username);

        return mapToInspectionPlanResponse(updatedPlan);
    }

    // Helper method to generate plan number
    private String generatePlanNumber() {
        long count = inspectionPlanRepository.count() + 1;
        return "QP-" + java.time.LocalDateTime.now().getYear() + "-" + String.format("%03d", count);
    }

    // Mapping helper
    private InspectionPlanResponse mapToInspectionPlanResponse(InspectionPlan inspectionPlan) {
        InspectionPlanResponse response = new InspectionPlanResponse();
        response.setId(inspectionPlan.getId());
        response.setPlanNumber(inspectionPlan.getPlanNumber());
        response.setTitle(inspectionPlan.getTitle());
        response.setDescription(inspectionPlan.getDescription());
        response.setInspectionType(inspectionPlan.getInspectionType());
        response.setApplicableStandards(inspectionPlan.getApplicableStandards());
        response.setInspectionFrequencyDays(inspectionPlan.getInspectionFrequencyDays());
        response.setSampleSize(inspectionPlan.getSampleSize());
        response.setIsActive(inspectionPlan.getIsActive());
        response.setCreatedAt(inspectionPlan.getCreatedAt());
        response.setUpdatedAt(inspectionPlan.getUpdatedAt());

        if (inspectionPlan.getQualityStandard() != null) {
            response.setQualityStandardId(inspectionPlan.getQualityStandard().getId());
            response.setQualityStandardName(inspectionPlan.getQualityStandard().getStandardName());
        }

        // Map checkpoints
        if (inspectionPlan.getCheckpoints() != null) {
            List<InspectionPlanResponse.QualityCheckResponse> checkpointResponses =
                    inspectionPlan.getCheckpoints().stream()
                            .map(checkpoint -> {
                                InspectionPlanResponse.QualityCheckResponse checkpointResponse =
                                        new InspectionPlanResponse.QualityCheckResponse();
                                checkpointResponse.setId(checkpoint.getId());
                                checkpointResponse.setCheckCode(checkpoint.getCheckCode());
                                checkpointResponse.setDescription(checkpoint.getDescription());
                                checkpointResponse.setAcceptanceCriteria(checkpoint.getAcceptanceCriteria());
                                checkpointResponse.setMeasurementMethod(checkpoint.getMeasurementMethod());
                                checkpointResponse.setToolsRequired(checkpoint.getToolsRequired());
                                checkpointResponse.setIsCritical(checkpoint.getIsCritical());
                                checkpointResponse.setSequenceNumber(checkpoint.getSequenceNumber());
                                return checkpointResponse;
                            })
                            .collect(Collectors.toList());
            response.setCheckpoints(checkpointResponses);
        }

        return response;
    }
}