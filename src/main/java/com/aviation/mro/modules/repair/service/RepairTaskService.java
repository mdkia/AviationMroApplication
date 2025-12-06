package com.aviation.mro.modules.repair.service;

import com.aviation.mro.modules.repair.domain.dto.*;
import com.aviation.mro.modules.repair.domain.model.RepairTask;
import com.aviation.mro.modules.repair.domain.model.WorkOrder;
import com.aviation.mro.modules.repair.domain.enums.TaskStatus;
import com.aviation.mro.modules.repair.repository.RepairTaskRepository;
import com.aviation.mro.modules.repair.repository.WorkOrderRepository;
import com.aviation.mro.modules.auth.model.User;
import com.aviation.mro.modules.auth.repository.UserRepository;
import com.aviation.mro.modules.parts.domain.model.AircraftPart;
import com.aviation.mro.modules.parts.repository.AircraftPartRepository;
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
public class RepairTaskService {

    private final RepairTaskRepository repairTaskRepository;
    private final WorkOrderRepository workOrderRepository;
    private final UserRepository userRepository;
    private final AircraftPartRepository aircraftPartRepository;

    @Transactional
    public RepairTaskResponse createRepairTask(RepairTaskRequest request, String username) {
        WorkOrder workOrder = workOrderRepository.findById(request.getWorkOrderId())
                .orElseThrow(() -> new NotFoundException("Work order not found with id: " + request.getWorkOrderId()));

        RepairTask repairTask = new RepairTask();
        repairTask.setTaskCode(request.getTaskCode());
        repairTask.setTitle(request.getTitle());
        repairTask.setDescription(request.getDescription());
        repairTask.setWorkOrder(workOrder);
        repairTask.setEstimatedHours(request.getEstimatedHours());
        repairTask.setEstimatedCost(request.getEstimatedCost());
        repairTask.setTechnicalReference(request.getTechnicalReference());
        repairTask.setToolRequirements(request.getToolRequirements());
        repairTask.setSafetyPrecautions(request.getSafetyPrecautions());
        repairTask.setPlannedStartDate(request.getPlannedStartDate());
        repairTask.setPlannedEndDate(request.getPlannedEndDate());
        repairTask.setCreatedByUser(username);

        // Assign technician if provided
        if (request.getAssignedTechnicianId() != null) {
            User technician = userRepository.findById(request.getAssignedTechnicianId())
                    .orElseThrow(() -> new NotFoundException("Technician not found"));
            repairTask.setAssignedTechnician(technician);
        }

        // Add required parts
        if (request.getRequiredPartIds() != null && !request.getRequiredPartIds().isEmpty()) {
            List<AircraftPart> parts = aircraftPartRepository.findAllById(request.getRequiredPartIds());
            repairTask.setRequiredParts(parts);
        }

        RepairTask savedTask = repairTaskRepository.save(repairTask);
        log.info("Repair task created for work order: {} by user: {}",
                workOrder.getWorkOrderNumber(), username);

        return mapToRepairTaskResponse(savedTask);
    }

    @Transactional(readOnly = true)
    public List<RepairTaskResponse> getTasksByWorkOrder(Long workOrderId) {
        return repairTaskRepository.findByWorkOrderId(workOrderId).stream()
                .map(this::mapToRepairTaskResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RepairTaskResponse> getTasksByTechnician(Long technicianId) {
        return repairTaskRepository.findByAssignedTechnicianId(technicianId).stream()
                .map(this::mapToRepairTaskResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RepairTaskResponse updateTaskStatus(Long taskId, TaskStatus status, String username) {
        RepairTask repairTask = repairTaskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Repair task not found with id: " + taskId));

        repairTask.setStatus(status);
        repairTask.setUpdatedByUser(username);

        // Set actual dates based on status changes
        if (status == TaskStatus.IN_PROGRESS && repairTask.getActualStartDate() == null) {
            repairTask.setActualStartDate(LocalDateTime.now());
        } else if (status == TaskStatus.COMPLETED && repairTask.getActualEndDate() == null) {
            repairTask.setActualEndDate(LocalDateTime.now());
        }

        RepairTask updatedTask = repairTaskRepository.save(repairTask);
        log.info("Repair task status updated: {} to {} by user: {}",
                repairTask.getTaskCode(), status, username);

        return mapToRepairTaskResponse(updatedTask);
    }

    @Transactional
    public RepairTaskResponse assignTechnicianToTask(Long taskId, Long technicianId, String username) {
        RepairTask repairTask = repairTaskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Repair task not found with id: " + taskId));

        User technician = userRepository.findById(technicianId)
                .orElseThrow(() -> new NotFoundException("Technician not found with id: " + technicianId));

        repairTask.setAssignedTechnician(technician);
        repairTask.setUpdatedByUser(username);

        RepairTask updatedTask = repairTaskRepository.save(repairTask);
        log.info("Technician assigned to repair task: {} by user: {}",
                repairTask.getTaskCode(), username);

        return mapToRepairTaskResponse(updatedTask);
    }

    // Mapping helper
    private RepairTaskResponse mapToRepairTaskResponse(RepairTask repairTask) {
        RepairTaskResponse response = new RepairTaskResponse();
        response.setId(repairTask.getId());
        response.setTaskCode(repairTask.getTaskCode());
        response.setTitle(repairTask.getTitle());
        response.setDescription(repairTask.getDescription());
        response.setStatus(repairTask.getStatus());
        response.setEstimatedHours(repairTask.getEstimatedHours());
        response.setActualHours(repairTask.getActualHours());
        response.setEstimatedCost(repairTask.getEstimatedCost());
        response.setActualCost(repairTask.getActualCost());
        response.setTechnicalReference(repairTask.getTechnicalReference());
        response.setToolRequirements(repairTask.getToolRequirements());
        response.setSafetyPrecautions(repairTask.getSafetyPrecautions());
        response.setPlannedStartDate(repairTask.getPlannedStartDate());
        response.setPlannedEndDate(repairTask.getPlannedEndDate());
        response.setActualStartDate(repairTask.getActualStartDate());
        response.setActualEndDate(repairTask.getActualEndDate());
        response.setCreatedAt(repairTask.getCreatedAt());
        response.setUpdatedAt(repairTask.getUpdatedAt());

        // Work order info
        if (repairTask.getWorkOrder() != null) {
            response.setWorkOrderId(repairTask.getWorkOrder().getId());
            response.setWorkOrderNumber(repairTask.getWorkOrder().getWorkOrderNumber());
        }

        // Assigned technician
        if (repairTask.getAssignedTechnician() != null) {
            response.setAssignedTechnician(repairTask.getAssignedTechnician().getUsername());
        }

        // Required parts
        if (repairTask.getRequiredParts() != null) {
            List<RepairTaskResponse.PartInfo> partInfos = repairTask.getRequiredParts().stream()
                    .map(part -> {
                        RepairTaskResponse.PartInfo partInfo = new RepairTaskResponse.PartInfo();
                        partInfo.setId(part.getId());
                        partInfo.setPartNumber(part.getPartNumber());
                        partInfo.setDescription(part.getDescription());
                        return partInfo;
                    })
                    .collect(Collectors.toList());
            response.setRequiredParts(partInfos);
        }

        return response;
    }
}