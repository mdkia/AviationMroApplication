package com.aviation.mro.modules.quality.controller;

//import com.aviation.mro.modules.quality.domain.dto.InspectionRequest;
//import com.aviation.mro.modules.quality.domain.dto.InspectionResponse;
//import com.aviation.mro.modules.quality.domain.enums.InspectionStatus;
//import com.aviation.mro.modules.quality.service.InspectionService;
//import com.aviation.mro.shared.common.ApiResponse;
//import com.aviation.mro.shared.security.SecurityUtils;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/quality/inspections")
//@RequiredArgsConstructor
//@Tag(name = "Inspection Management", description = "APIs for managing quality inspections")
//public class InspectionController {
//
//    private final InspectionService inspectionService;
//
//    @PostMapping
//    @Operation(summary = "Create a new inspection")
//    public ResponseEntity<ApiResponse> createInspection(
//            @Valid @RequestBody InspectionRequest request,
//            Authentication authentication) {
//        String username = SecurityUtils.getCurrentUsername(authentication);
//        InspectionResponse response = inspectionService.createInspection(request, username);
//        return ResponseEntity.ok(ApiResponse.success("Inspection created successfully", response));
//    }
//
//    @PutMapping("/{id}/complete")
//    @Operation(summary = "Complete inspection")
//    public ResponseEntity<ApiResponse> completeInspection(
//            @PathVariable Long id,
//            @RequestParam String findings,
//            @RequestParam String recommendations,
//            Authentication authentication) {
//        String username = SecurityUtils.getCurrentUsername(authentication);
//        InspectionResponse response = inspectionService.completeInspection(id, findings, recommendations, username);
//        return ResponseEntity.ok(ApiResponse.success("Inspection completed successfully", response));
//    }
//
//    @GetMapping
//    @Operation(summary = "Get all inspections")
//    public ResponseEntity<ApiResponse> getAllInspections() {
//        List<InspectionResponse> inspections = inspectionService.getAllInspections();
//        return ResponseEntity.ok(ApiResponse.success("Inspections retrieved successfully", inspections));
//    }
//
//    @GetMapping("/{id}")
//    @Operation(summary = "Get inspection by ID")
//    public ResponseEntity<ApiResponse> getInspectionById(@PathVariable Long id) {
//        InspectionResponse inspection = inspectionService.getInspectionById(id);
//        return ResponseEntity.ok(ApiResponse.success("Inspection retrieved successfully", inspection));
//    }
//
//    @GetMapping("/number/{inspectionNumber}")
//    @Operation(summary = "Get inspection by number")
//    public ResponseEntity<ApiResponse> getInspectionByNumber(@PathVariable String inspectionNumber) {
//        InspectionResponse inspection = inspectionService.getInspectionByNumber(inspectionNumber);
//        return ResponseEntity.ok(ApiResponse.success("Inspection retrieved successfully", inspection));
//    }
//
//    @GetMapping("/status/{status}")
//    @Operation(summary = "Get inspections by status")
//    public ResponseEntity<ApiResponse> getInspectionsByStatus(@PathVariable InspectionStatus status) {
//        List<InspectionResponse> inspections = inspectionService.getInspectionsByStatus(status);
//        return ResponseEntity.ok(ApiResponse.success("Inspections retrieved successfully", inspections));
//    }
//
//    @GetMapping("/part/{partId}")
//    @Operation(summary = "Get inspections by part")
//    public ResponseEntity<ApiResponse> getInspectionsByPart(@PathVariable Long partId) {
//        List<InspectionResponse> inspections = inspectionService.getInspectionsByPart(partId);
//        return ResponseEntity.ok(ApiResponse.success("Inspections retrieved successfully", inspections));
//    }
//
//    @GetMapping("/work-order/{workOrderId}")
//    @Operation(summary = "Get inspections by work order")
//    public ResponseEntity<ApiResponse> getInspectionsByWorkOrder(@PathVariable Long workOrderId) {
//        List<InspectionResponse> inspections = inspectionService.getInspectionsByWorkOrder(workOrderId);
//        return ResponseEntity.ok(ApiResponse.success("Inspections retrieved successfully", inspections));
//    }
//
//    @GetMapping("/in-progress")
//    @Operation(summary = "Get in-progress inspections")
//    public ResponseEntity<ApiResponse> getInProgressInspections() {
//        List<InspectionResponse> inspections = inspectionService.getInProgressInspections();
//        return ResponseEntity.ok(ApiResponse.success("In-progress inspections retrieved successfully", inspections));
//    }
//}

import com.aviation.mro.modules.quality.domain.dto.InspectionRequest;
import com.aviation.mro.modules.quality.domain.dto.InspectionResponse;
import com.aviation.mro.modules.quality.domain.enums.InspectionStatus;
import com.aviation.mro.modules.quality.service.InspectionService;
import com.aviation.mro.modules.repair.domain.model.WorkOrder;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quality/inspections")
@RequiredArgsConstructor
@Tag(name = "Inspection Management", description = "APIs for managing quality inspections")
public class InspectionController {

    private final InspectionService inspectionService;

    @PostMapping
    @Operation(summary = "Create a new inspection")
    public ResponseEntity<ApiResponse> createInspection(
            @Valid @RequestBody InspectionRequest request,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        InspectionResponse response = inspectionService.createInspection(request, username);
        return ResponseEntity.ok(ApiResponse.success("Inspection created successfully", response));
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "Complete inspection")
    public ResponseEntity<ApiResponse> completeInspection(
            @PathVariable Long id,
            @RequestParam String findings,
            @RequestParam String recommendations,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        InspectionResponse response = inspectionService.completeInspection(id, findings, recommendations, username);
        return ResponseEntity.ok(ApiResponse.success("Inspection completed successfully", response));
    }

    @PostMapping("/{id}/create-repair-order")
    @Operation(summary = "Create repair work order from failed inspection")
    public ResponseEntity<ApiResponse> createRepairOrderFromInspection(
            @PathVariable Long id,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);

        try {
            WorkOrder workOrder = inspectionService.createManualRepairOrderFromInspection(id, username);
            return ResponseEntity.ok(ApiResponse.success("Repair work order created successfully", workOrder));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to create repair work order: " + e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Get all inspections")
    public ResponseEntity<ApiResponse> getAllInspections() {
        List<InspectionResponse> inspections = inspectionService.getAllInspections();
        return ResponseEntity.ok(ApiResponse.success("Inspections retrieved successfully", inspections));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get inspection by ID")
    public ResponseEntity<ApiResponse> getInspectionById(@PathVariable Long id) {
        InspectionResponse inspection = inspectionService.getInspectionById(id);
        return ResponseEntity.ok(ApiResponse.success("Inspection retrieved successfully", inspection));
    }

    @GetMapping("/number/{inspectionNumber}")
    @Operation(summary = "Get inspection by number")
    public ResponseEntity<ApiResponse> getInspectionByNumber(@PathVariable String inspectionNumber) {
        InspectionResponse inspection = inspectionService.getInspectionByNumber(inspectionNumber);
        return ResponseEntity.ok(ApiResponse.success("Inspection retrieved successfully", inspection));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get inspections by status")
    public ResponseEntity<ApiResponse> getInspectionsByStatus(@PathVariable InspectionStatus status) {
        List<InspectionResponse> inspections = inspectionService.getInspectionsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Inspections retrieved successfully", inspections));
    }

    @GetMapping("/part/{partId}")
    @Operation(summary = "Get inspections by part")
    public ResponseEntity<ApiResponse> getInspectionsByPart(@PathVariable Long partId) {
        List<InspectionResponse> inspections = inspectionService.getInspectionsByPart(partId);
        return ResponseEntity.ok(ApiResponse.success("Inspections retrieved successfully", inspections));
    }

    @GetMapping("/work-order/{workOrderId}")
    @Operation(summary = "Get inspections by work order")
    public ResponseEntity<ApiResponse> getInspectionsByWorkOrder(@PathVariable Long workOrderId) {
        List<InspectionResponse> inspections = inspectionService.getInspectionsByWorkOrder(workOrderId);
        return ResponseEntity.ok(ApiResponse.success("Inspections retrieved successfully", inspections));
    }

    @GetMapping("/in-progress")
    @Operation(summary = "Get in-progress inspections")
    public ResponseEntity<ApiResponse> getInProgressInspections() {
        List<InspectionResponse> inspections = inspectionService.getInProgressInspections();
        return ResponseEntity.ok(ApiResponse.success("In-progress inspections retrieved successfully", inspections));
    }

//    @GetMapping("/requires-repair")
//    @Operation(summary = "Get inspections that require repair")
//    public ResponseEntity<ApiResponse> getInspectionsRequiringRepair() {
//        List<InspectionResponse> allInspections = inspectionService.getAllInspections();
//        List<InspectionResponse> repairsNeeded = allInspections.stream()
//                .filter(inspection -> inspection.getComplianceStatus() == com.aviation.mro.modules.quality.domain.enums.ComplianceStatus.NON_COMPLIANT)
//                .filter(inspection -> inspection.getPartId() != null)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(ApiResponse.success("Inspections requiring repair retrieved successfully", repairsNeeded));
//    }

    @GetMapping("/requires-repair")
    @Operation(summary = "Get inspections that require repair")
    public ResponseEntity<ApiResponse> getInspectionsRequiringRepair() {
        List<InspectionResponse> allInspections = inspectionService.getAllInspections();
        List<InspectionResponse> repairsNeeded = allInspections.stream()
                .filter(inspection -> {
                    // فقط بازرسی‌های کامل شده که نیاز به تعمیر دارند
                    return inspection.getStatus() == InspectionStatus.COMPLETED &&
                            inspection.getComplianceStatus() == com.aviation.mro.modules.quality.domain.enums.ComplianceStatus.NON_COMPLIANT &&
                            inspection.getPartId() != null &&
                            inspection.getWorkOrderId() == null; // هنوز Work Order ندارند
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Inspections requiring repair retrieved successfully", repairsNeeded));
    }
}