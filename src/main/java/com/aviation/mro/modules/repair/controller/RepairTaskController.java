package com.aviation.mro.modules.repair.controller;

import com.aviation.mro.modules.repair.domain.dto.RepairTaskRequest;
import com.aviation.mro.modules.repair.domain.dto.RepairTaskResponse;
import com.aviation.mro.modules.repair.domain.enums.TaskStatus;
import com.aviation.mro.modules.repair.service.RepairTaskService;
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
@RequestMapping("/api/repair/tasks")
@RequiredArgsConstructor
@Tag(name = "Repair Task Management", description = "APIs for managing repair tasks")
public class RepairTaskController {

    private final RepairTaskService repairTaskService;

    @PostMapping
    @Operation(summary = "Create a new repair task")
    public ResponseEntity<ApiResponse> createRepairTask(
            @Valid @RequestBody RepairTaskRequest request,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        RepairTaskResponse response = repairTaskService.createRepairTask(request, username);
        return ResponseEntity.ok(ApiResponse.success("Repair task created successfully", response));
    }

    @PutMapping("/{taskId}/status")
    @Operation(summary = "Update task status")
    public ResponseEntity<ApiResponse> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam TaskStatus status,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        RepairTaskResponse task = repairTaskService.updateTaskStatus(taskId, status, username);
        return ResponseEntity.ok(ApiResponse.success("Task status updated successfully", task));
    }

    @PutMapping("/{taskId}/assign-technician")
    @Operation(summary = "Assign technician to task")
    public ResponseEntity<ApiResponse> assignTechnicianToTask(
            @PathVariable Long taskId,
            @RequestParam Long technicianId,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        RepairTaskResponse task = repairTaskService.assignTechnicianToTask(taskId, technicianId, username);
        return ResponseEntity.ok(ApiResponse.success("Technician assigned successfully", task));
    }

//    @PostMapping
//    @Operation(summary = "Create a new repair task")
//    public ResponseEntity<ApiResponse> createRepairTask(
//            @Valid @RequestBody RepairTaskRequest request,
//            @AuthenticationPrincipal UserDetails userDetails) {
//        RepairTaskResponse response = repairTaskService.createRepairTask(request, userDetails.getUsername());
//        return ResponseEntity.ok(ApiResponse.success("Repair task created successfully", response));
//    }

    @GetMapping("/work-order/{workOrderId}")
    @Operation(summary = "Get tasks by work order")
    public ResponseEntity<ApiResponse> getTasksByWorkOrder(
            @PathVariable Long workOrderId) {
        List<RepairTaskResponse> tasks = repairTaskService.getTasksByWorkOrder(workOrderId);
        return ResponseEntity.ok(ApiResponse.success("Tasks retrieved successfully", tasks));
    }

    @GetMapping("/technician/{technicianId}")
    @Operation(summary = "Get tasks by technician")
    public ResponseEntity<ApiResponse> getTasksByTechnician(
            @PathVariable Long technicianId) {
        List<RepairTaskResponse> tasks = repairTaskService.getTasksByTechnician(technicianId);
        return ResponseEntity.ok(ApiResponse.success("Tasks retrieved successfully", tasks));
    }

//    @PutMapping("/{taskId}/status")
//    @Operation(summary = "Update task status")
//    public ResponseEntity<ApiResponse> updateTaskStatus(
//            @PathVariable Long taskId,
//            @RequestParam TaskStatus status,
//            @AuthenticationPrincipal UserDetails userDetails) {
//        RepairTaskResponse task = repairTaskService.updateTaskStatus(taskId, status, userDetails.getUsername());
//        return ResponseEntity.ok(ApiResponse.success("Task status updated successfully", task));
//    }

//    @PutMapping("/{taskId}/assign-technician")
//    @Operation(summary = "Assign technician to task")
//    public ResponseEntity<ApiResponse> assignTechnicianToTask(
//            @PathVariable Long taskId,
//            @RequestParam Long technicianId,
//            @AuthenticationPrincipal UserDetails userDetails) {
//        RepairTaskResponse task = repairTaskService.assignTechnicianToTask(taskId, technicianId, userDetails.getUsername());
//        return ResponseEntity.ok(ApiResponse.success("Technician assigned successfully", task));
//    }
}