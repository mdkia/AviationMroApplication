package com.aviation.mro.modules.quality.controller;

import com.aviation.mro.modules.quality.domain.dto.InspectionPlanRequest;
import com.aviation.mro.modules.quality.domain.dto.InspectionPlanResponse;
import com.aviation.mro.modules.quality.service.InspectionPlanService;
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
@RequestMapping("/api/quality/inspection-plans")
@RequiredArgsConstructor
@Tag(name = "Inspection Plan Management", description = "APIs for managing inspection plans")
public class InspectionPlanController {

    private final InspectionPlanService inspectionPlanService;

    @PostMapping
    @Operation(summary = "Create a new inspection plan")
    public ResponseEntity<ApiResponse> createInspectionPlan(
            @Valid @RequestBody InspectionPlanRequest request,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        InspectionPlanResponse response = inspectionPlanService.createInspectionPlan(request, username);
        return ResponseEntity.ok(ApiResponse.success("Inspection plan created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all inspection plans")
    public ResponseEntity<ApiResponse> getAllInspectionPlans() {
        List<InspectionPlanResponse> plans = inspectionPlanService.getAllInspectionPlans();
        return ResponseEntity.ok(ApiResponse.success("Inspection plans retrieved successfully", plans));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get inspection plan by ID")
    public ResponseEntity<ApiResponse> getInspectionPlanById(@PathVariable Long id) {
        InspectionPlanResponse plan = inspectionPlanService.getInspectionPlanById(id);
        return ResponseEntity.ok(ApiResponse.success("Inspection plan retrieved successfully", plan));
    }

    @GetMapping("/number/{planNumber}")
    @Operation(summary = "Get inspection plan by number")
    public ResponseEntity<ApiResponse> getInspectionPlanByNumber(@PathVariable String planNumber) {
        InspectionPlanResponse plan = inspectionPlanService.getInspectionPlanByNumber(planNumber);
        return ResponseEntity.ok(ApiResponse.success("Inspection plan retrieved successfully", plan));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active inspection plans")
    public ResponseEntity<ApiResponse> getActiveInspectionPlans() {
        List<InspectionPlanResponse> plans = inspectionPlanService.getActiveInspectionPlans();
        return ResponseEntity.ok(ApiResponse.success("Active inspection plans retrieved successfully", plans));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update inspection plan status")
    public ResponseEntity<ApiResponse> updatePlanStatus(
            @PathVariable Long id,
            @RequestParam Boolean isActive,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        InspectionPlanResponse plan = inspectionPlanService.updatePlanStatus(id, isActive, username);
        return ResponseEntity.ok(ApiResponse.success("Inspection plan status updated successfully", plan));
    }
}
