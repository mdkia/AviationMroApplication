package com.aviation.mro.modules.quality.service;

import com.aviation.mro.modules.quality.domain.dto.NonConformanceRequest;
import com.aviation.mro.modules.quality.domain.dto.NonConformanceResponse;
import com.aviation.mro.modules.quality.domain.model.*;
import com.aviation.mro.modules.quality.domain.enums.NCRStatus;
import com.aviation.mro.modules.quality.domain.enums.CorrectiveActionStatus;
import com.aviation.mro.modules.quality.repository.*;
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
public class NonConformanceService {

    private final NonConformanceReportRepository nonConformanceReportRepository;
    private final InspectionRepository inspectionRepository;

    @Transactional
    public NonConformanceResponse createNonConformanceReport(NonConformanceRequest request, String username) {
        Inspection inspection = inspectionRepository.findById(request.getInspectionId())
                .orElseThrow(() -> new NotFoundException("Inspection not found with id: " + request.getInspectionId()));

        // Check if NCR already exists for this inspection
        if (inspection.getNonConformanceReport() != null) {
            throw new IllegalStateException("NCR already exists for inspection: " + inspection.getInspectionNumber());
        }

        // Generate NCR number
        String ncrNumber = generateNCRNumber();

        NonConformanceReport ncr = new NonConformanceReport();
        ncr.setNcrNumber(ncrNumber);
        ncr.setInspection(inspection);
        ncr.setProblemDescription(request.getProblemDescription());
        ncr.setRootCause(request.getRootCause());
        ncr.setImmediateAction(request.getImmediateAction());
        ncr.setAssignedTo(request.getAssignedTo());
        ncr.setTargetCompletionDate(request.getTargetCompletionDate());
        ncr.setRaisedBy(username);

        NonConformanceReport savedNCR = nonConformanceReportRepository.save(ncr);

        // Link NCR to inspection
        inspection.setNonConformanceReport(savedNCR);
        inspectionRepository.save(inspection);

        log.info("Non-conformance report created: {} by user: {}", ncrNumber, username);

        return mapToNonConformanceResponse(savedNCR);
    }

    @Transactional
    public NonConformanceResponse updateNCRStatus(Long ncrId, NCRStatus status, String username) {
        NonConformanceReport ncr = nonConformanceReportRepository.findById(ncrId)
                .orElseThrow(() -> new NotFoundException("NCR not found with id: " + ncrId));

        ncr.setStatus(status);

        if (status == NCRStatus.CLOSED) {
            ncr.setActualCompletionDate(LocalDateTime.now());
        }

        NonConformanceReport updatedNCR = nonConformanceReportRepository.save(ncr);
        log.info("NCR status updated: {} to {} by user: {}", ncr.getNcrNumber(), status, username);

        return mapToNonConformanceResponse(updatedNCR);
    }

    @Transactional
    public NonConformanceResponse updateCorrectiveAction(Long ncrId, String correctiveAction, String preventiveAction,
                                                         CorrectiveActionStatus actionStatus, String username) {
        NonConformanceReport ncr = nonConformanceReportRepository.findById(ncrId)
                .orElseThrow(() -> new NotFoundException("NCR not found with id: " + ncrId));

        ncr.setCorrectiveAction(correctiveAction);
        ncr.setPreventiveAction(preventiveAction);
        ncr.setCorrectiveActionStatus(actionStatus);
        ncr.setAssignedTo(username);

        NonConformanceReport updatedNCR = nonConformanceReportRepository.save(ncr);
        log.info("Corrective action updated for NCR: {} by user: {}", ncr.getNcrNumber(), username);

        return mapToNonConformanceResponse(updatedNCR);
    }

    @Transactional
    public NonConformanceResponse verifyNCR(Long ncrId, Boolean isEffective, String verificationNotes, String username) {
        NonConformanceReport ncr = nonConformanceReportRepository.findById(ncrId)
                .orElseThrow(() -> new NotFoundException("NCR not found with id: " + ncrId));

        ncr.setIsEffective(isEffective);
        ncr.setVerificationNotes(verificationNotes);
        ncr.setVerifiedBy(username);
        ncr.setVerificationDate(LocalDateTime.now());
        ncr.setCorrectiveActionStatus(CorrectiveActionStatus.VERIFIED);

        if (isEffective) {
            ncr.setStatus(NCRStatus.CLOSED);
        } else {
            ncr.setStatus(NCRStatus.REOPENED);
            ncr.setCorrectiveActionStatus(CorrectiveActionStatus.NOT_EFFECTIVE);
        }

        NonConformanceReport verifiedNCR = nonConformanceReportRepository.save(ncr);
        log.info("NCR verification completed: {} by user: {}", ncr.getNcrNumber(), username);

        return mapToNonConformanceResponse(verifiedNCR);
    }

    @Transactional(readOnly = true)
    public List<NonConformanceResponse> getAllNCRs() {
        return nonConformanceReportRepository.findAll().stream()
                .map(this::mapToNonConformanceResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NonConformanceResponse getNCRById(Long id) {
        NonConformanceReport ncr = nonConformanceReportRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("NCR not found with id: " + id));
        return mapToNonConformanceResponse(ncr);
    }

    @Transactional(readOnly = true)
    public NonConformanceResponse getNCRByNumber(String ncrNumber) {
        NonConformanceReport ncr = nonConformanceReportRepository.findByNcrNumber(ncrNumber)
                .orElseThrow(() -> new NotFoundException("NCR not found: " + ncrNumber));
        return mapToNonConformanceResponse(ncr);
    }

    @Transactional(readOnly = true)
    public List<NonConformanceResponse> getNCRsByStatus(NCRStatus status) {
        return nonConformanceReportRepository.findByStatus(status).stream()
                .map(this::mapToNonConformanceResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NonConformanceResponse> getOverdueNCRs() {
        return nonConformanceReportRepository.findOverdueNCRs(LocalDateTime.now()).stream()
                .map(this::mapToNonConformanceResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long getOpenNCRCount() {
        return nonConformanceReportRepository.countOpenNCRs();
    }

    // Helper method to generate NCR number
    private String generateNCRNumber() {
        long count = nonConformanceReportRepository.count() + 1;
        return "NCR-" + LocalDateTime.now().getYear() + "-" + String.format("%03d", count);
    }

    // Mapping helper
    private NonConformanceResponse mapToNonConformanceResponse(NonConformanceReport ncr) {
        NonConformanceResponse response = new NonConformanceResponse();
        response.setId(ncr.getId());
        response.setNcrNumber(ncr.getNcrNumber());
        response.setInspectionId(ncr.getInspection().getId());
        response.setInspectionNumber(ncr.getInspection().getInspectionNumber());
        response.setStatus(ncr.getStatus());
        response.setCorrectiveActionStatus(ncr.getCorrectiveActionStatus());
        response.setProblemDescription(ncr.getProblemDescription());
        response.setRootCause(ncr.getRootCause());
        response.setImmediateAction(ncr.getImmediateAction());
        response.setCorrectiveAction(ncr.getCorrectiveAction());
        response.setPreventiveAction(ncr.getPreventiveAction());
        response.setRaisedBy(ncr.getRaisedBy());
        response.setAssignedTo(ncr.getAssignedTo());
        response.setVerifiedBy(ncr.getVerifiedBy());
        response.setTargetCompletionDate(ncr.getTargetCompletionDate());
        response.setActualCompletionDate(ncr.getActualCompletionDate());
        response.setVerificationDate(ncr.getVerificationDate());
        response.setIsEffective(ncr.getIsEffective());
        response.setVerificationNotes(ncr.getVerificationNotes());
        response.setCreatedAt(ncr.getCreatedAt());
        response.setUpdatedAt(ncr.getUpdatedAt());

        // Map part info from inspection
        if (ncr.getInspection().getPart() != null) {
            response.setPartId(ncr.getInspection().getPart().getId());
            response.setPartNumber(ncr.getInspection().getPart().getPartNumber());
        }

        return response;
    }
}
