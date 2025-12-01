// modules/quality/controller/NonConformanceController.java
package com.aviation.mro.modules.quality.controller;

import com.aviation.mro.modules.quality.domain.dto.NonConformanceRequest;
import com.aviation.mro.modules.quality.domain.dto.NonConformanceResponse;
import com.aviation.mro.modules.quality.domain.enums.NCRStatus;
import com.aviation.mro.modules.quality.domain.enums.CorrectiveActionStatus;
import com.aviation.mro.modules.quality.service.NonConformanceService;
import com.aviation.mro.shared.common.ApiResponse;
import com.aviation.mro.shared.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quality/non-conformances")
@RequiredArgsConstructor
@Tag(name = "Non-Conformance Management", description = "APIs for managing non-conformance reports")
public class NonConformanceController {

    private final NonConformanceService nonConformanceService;

    @PostMapping
    @Operation(summary = "Create a new non-conformance report")
    public ResponseEntity<ApiResponse> createNonConformanceReport(
            @Valid @RequestBody NonConformanceRequest request,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        NonConformanceResponse response = nonConformanceService.createNonConformanceReport(request, username);
        return ResponseEntity.ok(ApiResponse.success("Non-conformance report created successfully", response));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update NCR status")
    public ResponseEntity<ApiResponse> updateNCRStatus(
            @PathVariable Long id,
            @RequestParam NCRStatus status,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        NonConformanceResponse response = nonConformanceService.updateNCRStatus(id, status, username);
        return ResponseEntity.ok(ApiResponse.success("NCR status updated successfully", response));
    }

    @PutMapping("/{id}/corrective-action")
    @Operation(summary = "Update corrective action")
    public ResponseEntity<ApiResponse> updateCorrectiveAction(
            @PathVariable Long id,
            @RequestParam String correctiveAction,
            @RequestParam String preventiveAction,
            @RequestParam CorrectiveActionStatus actionStatus,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        NonConformanceResponse response = nonConformanceService.updateCorrectiveAction(
                id, correctiveAction, preventiveAction, actionStatus, username);
        return ResponseEntity.ok(ApiResponse.success("Corrective action updated successfully", response));
    }

    @PutMapping("/{id}/verify")
    @Operation(summary = "Verify NCR")
    public ResponseEntity<ApiResponse> verifyNCR(
            @PathVariable Long id,
            @RequestParam Boolean isEffective,
            @RequestParam String verificationNotes,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        NonConformanceResponse response = nonConformanceService.verifyNCR(id, isEffective, verificationNotes, username);
        return ResponseEntity.ok(ApiResponse.success("NCR verification completed successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all NCRs")
    public ResponseEntity<ApiResponse> getAllNCRs() {
        List<NonConformanceResponse> ncrs = nonConformanceService.getAllNCRs();
        return ResponseEntity.ok(ApiResponse.success("NCRs retrieved successfully", ncrs));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get NCR by ID")
    public ResponseEntity<ApiResponse> getNCRById(@PathVariable Long id) {
        NonConformanceResponse ncr = nonConformanceService.getNCRById(id);
        return ResponseEntity.ok(ApiResponse.success("NCR retrieved successfully", ncr));
    }

    @GetMapping("/number/{ncrNumber}")
    @Operation(summary = "Get NCR by number")
    public ResponseEntity<ApiResponse> getNCRByNumber(@PathVariable String ncrNumber) {
        NonConformanceResponse ncr = nonConformanceService.getNCRByNumber(ncrNumber);
        return ResponseEntity.ok(ApiResponse.success("NCR retrieved successfully", ncr));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get NCRs by status")
    public ResponseEntity<ApiResponse> getNCRsByStatus(@PathVariable NCRStatus status) {
        List<NonConformanceResponse> ncrs = nonConformanceService.getNCRsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("NCRs retrieved successfully", ncrs));
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue NCRs")
    public ResponseEntity<ApiResponse> getOverdueNCRs() {
        List<NonConformanceResponse> ncrs = nonConformanceService.getOverdueNCRs();
        return ResponseEntity.ok(ApiResponse.success("Overdue NCRs retrieved successfully", ncrs));
    }

    @GetMapping("/stats/open-count")
    @Operation(summary = "Get open NCR count")
    public ResponseEntity<ApiResponse> getOpenNCRCount() {
        Long count = nonConformanceService.getOpenNCRCount();
        return ResponseEntity.ok(ApiResponse.success("Open NCR count retrieved successfully", count));
    }
}