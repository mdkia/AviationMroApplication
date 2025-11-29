package com.aviation.mro.modules.repair.controller;

import com.aviation.mro.modules.repair.domain.dto.WorkOrderRequest;
import com.aviation.mro.modules.repair.domain.dto.WorkOrderResponse;
import com.aviation.mro.modules.repair.domain.enums.WorkOrderStatus;
import com.aviation.mro.modules.repair.service.WorkOrderService;
import com.aviation.mro.shared.common.ApiResponse;
import com.aviation.mro.shared.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repair/work-orders")
@RequiredArgsConstructor
@Tag(name = "Work Order Management", description = "APIs for managing maintenance work orders")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    @PostMapping
    @Operation(summary = "Create a new work order")
    public ResponseEntity<ApiResponse> createWorkOrder(
            @Valid @RequestBody WorkOrderRequest request,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        WorkOrderResponse response = workOrderService.createWorkOrder(request, username);
        return ResponseEntity.ok(ApiResponse.success("Work order created successfully", response));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update work order status")
    public ResponseEntity<ApiResponse> updateWorkOrderStatus(
            @PathVariable Long id,
            @RequestParam WorkOrderStatus status,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        WorkOrderResponse workOrder = workOrderService.updateWorkOrderStatus(id, status, username);
        return ResponseEntity.ok(ApiResponse.success("Work order status updated successfully", workOrder));
    }

    @PutMapping("/{id}/assign-technician")
    @Operation(summary = "Assign technician to work order")
    public ResponseEntity<ApiResponse> assignTechnician(
            @PathVariable Long id,
            @RequestParam Long technicianId,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        WorkOrderResponse workOrder = workOrderService.assignTechnician(id, technicianId, username);
        return ResponseEntity.ok(ApiResponse.success("Technician assigned successfully", workOrder));
    }

//    @PostMapping
//    @Operation(summary = "Create a new work order")
//    public ResponseEntity<ApiResponse> createWorkOrder(
//            @Valid @RequestBody WorkOrderRequest request,
//            Authentication authentication) {
//        String username = authentication.getName();
//        WorkOrderResponse response = workOrderService.createWorkOrder(request, username);
//        return ResponseEntity.ok(ApiResponse.success("Work order created successfully", response));
//    }

    @GetMapping
    @Operation(summary = "Get all work orders")
    public ResponseEntity<ApiResponse> getAllWorkOrders() {
        List<WorkOrderResponse> workOrders = workOrderService.getAllWorkOrders();
        return ResponseEntity.ok(ApiResponse.success("Work orders retrieved successfully", workOrders));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get work order by ID")
    public ResponseEntity<ApiResponse> getWorkOrderById(@PathVariable Long id) {
        WorkOrderResponse workOrder = workOrderService.getWorkOrderById(id);
        return ResponseEntity.ok(ApiResponse.success("Work order retrieved successfully", workOrder));
    }

    @GetMapping("/number/{workOrderNumber}")
    @Operation(summary = "Get work order by number")
    public ResponseEntity<ApiResponse> getWorkOrderByNumber(
            @PathVariable String workOrderNumber) {
        WorkOrderResponse workOrder = workOrderService.getWorkOrderByNumber(workOrderNumber);
        return ResponseEntity.ok(ApiResponse.success("Work order retrieved successfully", workOrder));
    }

//    @PutMapping("/{id}/status")
//    @Operation(summary = "Update work order status")
//    public ResponseEntity<ApiResponse> updateWorkOrderStatus(
//            @PathVariable Long id,
//            @RequestParam WorkOrderStatus status,
//            @AuthenticationPrincipal UserDetails userDetails) {
//        WorkOrderResponse workOrder = workOrderService.updateWorkOrderStatus(id, status, userDetails.getUsername());
//        return ResponseEntity.ok(ApiResponse.success("Work order status updated successfully", workOrder));
//    }

//    @PutMapping("/{id}/assign-technician")
//    @Operation(summary = "Assign technician to work order")
//    public ResponseEntity<ApiResponse> assignTechnician(
//            @PathVariable Long id,
//            @RequestParam Long technicianId,
//            @AuthenticationPrincipal UserDetails userDetails) {
//        WorkOrderResponse workOrder = workOrderService.assignTechnician(id, technicianId, userDetails.getUsername());
//        return ResponseEntity.ok(ApiResponse.success("Technician assigned successfully", workOrder));
//    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get work orders by status")
    public ResponseEntity<ApiResponse> getWorkOrdersByStatus(
            @PathVariable WorkOrderStatus status) {
        List<WorkOrderResponse> workOrders = workOrderService.getWorkOrdersByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Work orders retrieved successfully", workOrders));
    }

    @GetMapping("/technician/{technicianId}")
    @Operation(summary = "Get work orders by technician")
    public ResponseEntity<ApiResponse> getWorkOrdersByTechnician(
            @PathVariable Long technicianId) {
        List<WorkOrderResponse> workOrders = workOrderService.getWorkOrdersByTechnician(technicianId);
        return ResponseEntity.ok(ApiResponse.success("Work orders retrieved successfully", workOrders));
    }
}