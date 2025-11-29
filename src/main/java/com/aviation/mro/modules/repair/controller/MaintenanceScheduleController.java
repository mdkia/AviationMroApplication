package com.aviation.mro.modules.repair.controller;

import com.aviation.mro.modules.repair.domain.dto.MaintenanceScheduleRequest;
import com.aviation.mro.modules.repair.domain.dto.MaintenanceScheduleResponse;
import com.aviation.mro.modules.repair.service.MaintenanceScheduleService;
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
@RequestMapping("/api/repair/schedules")
@RequiredArgsConstructor
@Tag(name = "Maintenance Schedule Management", description = "APIs for managing maintenance schedules")
public class MaintenanceScheduleController {

    private final MaintenanceScheduleService maintenanceScheduleService;

    @PostMapping
    @Operation(summary = "Create a new maintenance schedule")
    public ResponseEntity<ApiResponse> createSchedule(
            @Valid @RequestBody MaintenanceScheduleRequest request,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        MaintenanceScheduleResponse response = maintenanceScheduleService.createSchedule(request, username);
        return ResponseEntity.ok(ApiResponse.success("Maintenance schedule created successfully", response));
    }

    @PutMapping("/{scheduleId}/link-work-order/{workOrderId}")
    @Operation(summary = "Link schedule to work order")
    public ResponseEntity<ApiResponse> linkToWorkOrder(
            @PathVariable Long scheduleId,
            @PathVariable Long workOrderId,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        MaintenanceScheduleResponse response = maintenanceScheduleService.linkToWorkOrder(scheduleId, workOrderId, username);
        return ResponseEntity.ok(ApiResponse.success("Schedule linked to work order successfully", response));
    }

    @GetMapping("/aircraft/{aircraftRegistration}")
    @Operation(summary = "Get schedules by aircraft")
    public ResponseEntity<ApiResponse> getSchedulesByAircraft(
            @PathVariable String aircraftRegistration) {
        List<MaintenanceScheduleResponse> schedules = maintenanceScheduleService.getSchedulesByAircraft(aircraftRegistration);
        return ResponseEntity.ok(ApiResponse.success("Schedules retrieved successfully", schedules));
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending schedules")
    public ResponseEntity<ApiResponse> getPendingSchedules() {
        List<MaintenanceScheduleResponse> schedules = maintenanceScheduleService.getPendingSchedules();
        return ResponseEntity.ok(ApiResponse.success("Pending schedules retrieved successfully", schedules));
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue schedules")
    public ResponseEntity<ApiResponse> getOverdueSchedules() {
        List<MaintenanceScheduleResponse> schedules = maintenanceScheduleService.getOverdueSchedules();
        return ResponseEntity.ok(ApiResponse.success("Overdue schedules retrieved successfully", schedules));
    }

    @PutMapping("/{scheduleId}/complete")
    @Operation(summary = "Mark schedule as completed")
    public ResponseEntity<ApiResponse> markAsCompleted(
            @PathVariable Long scheduleId,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        MaintenanceScheduleResponse response = maintenanceScheduleService.markAsCompleted(scheduleId, username);
        return ResponseEntity.ok(ApiResponse.success("Schedule marked as completed", response));
    }
}
