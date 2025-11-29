package com.aviation.mro.modules.repair.service;


import com.aviation.mro.modules.repair.domain.dto.*;
import com.aviation.mro.modules.repair.domain.model.WorkOrder;
import com.aviation.mro.modules.repair.domain.enums.WorkOrderStatus;
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
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final UserRepository userRepository;
    private final AircraftPartRepository aircraftPartRepository;

    @Transactional
    public WorkOrderResponse createWorkOrder(WorkOrderRequest request, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        // Generate work order number
        String workOrderNumber = generateWorkOrderNumber();

        WorkOrder workOrder = new WorkOrder();
        workOrder.setWorkOrderNumber(workOrderNumber);
        workOrder.setTitle(request.getTitle());
        workOrder.setDescription(request.getDescription());
        workOrder.setPriority(request.getPriority());
        workOrder.setMaintenanceType(request.getMaintenanceType());
        workOrder.setAircraftRegistration(request.getAircraftRegistration());
        workOrder.setAircraftType(request.getAircraftType());
        workOrder.setTailNumber(request.getTailNumber());
        workOrder.setEstimatedStartDate(request.getEstimatedStartDate());
        workOrder.setEstimatedCompletionDate(request.getEstimatedCompletionDate());
        workOrder.setEstimatedCost(request.getEstimatedCost());
        workOrder.setCreatedBy(currentUser);
        workOrder.setCreatedByUser(username);

        // Assign technician if provided
        if (request.getAssignedTechnicianId() != null) {
            User technician = userRepository.findById(request.getAssignedTechnicianId())
                    .orElseThrow(() -> new NotFoundException("Technician not found"));
            workOrder.setAssignedTechnician(technician);
        }

        // Add required parts
        if (request.getRequiredPartIds() != null && !request.getRequiredPartIds().isEmpty()) {
            List<AircraftPart> parts = aircraftPartRepository.findAllById(request.getRequiredPartIds());
            workOrder.setRequiredParts(parts);
        }

        WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);
        log.info("Work order created: {} by user: {}", workOrderNumber, username);

        return mapToWorkOrderResponse(savedWorkOrder);
    }

    @Transactional(readOnly = true)
    public List<WorkOrderResponse> getAllWorkOrders() {
        return workOrderRepository.findAll().stream()
                .map(this::mapToWorkOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WorkOrderResponse getWorkOrderById(Long id) {
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Work order not found with id: " + id));
        return mapToWorkOrderResponse(workOrder);
    }

    @Transactional(readOnly = true)
    public WorkOrderResponse getWorkOrderByNumber(String workOrderNumber) {
        WorkOrder workOrder = workOrderRepository.findByWorkOrderNumber(workOrderNumber)
                .orElseThrow(() -> new NotFoundException("Work order not found: " + workOrderNumber));
        return mapToWorkOrderResponse(workOrder);
    }

    @Transactional
    public WorkOrderResponse updateWorkOrderStatus(Long id, WorkOrderStatus status, String username) {
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Work order not found with id: " + id));

        workOrder.setStatus(status);
        workOrder.setUpdatedByUser(username);

        // Set actual dates based on status changes
        if (status == WorkOrderStatus.IN_PROGRESS && workOrder.getActualStartDate() == null) {
            workOrder.setActualStartDate(LocalDateTime.now());
        } else if (status == WorkOrderStatus.COMPLETED && workOrder.getActualCompletionDate() == null) {
            workOrder.setActualCompletionDate(LocalDateTime.now());
        }

        WorkOrder updatedWorkOrder = workOrderRepository.save(workOrder);
        log.info("Work order status updated: {} to {} by user: {}",
                workOrder.getWorkOrderNumber(), status, username);

        return mapToWorkOrderResponse(updatedWorkOrder);
    }

    @Transactional
    public WorkOrderResponse assignTechnician(Long workOrderId, Long technicianId, String username) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new NotFoundException("Work order not found with id: " + workOrderId));

        User technician = userRepository.findById(technicianId)
                .orElseThrow(() -> new NotFoundException("Technician not found with id: " + technicianId));

        workOrder.setAssignedTechnician(technician);
        workOrder.setUpdatedByUser(username);

        WorkOrder updatedWorkOrder = workOrderRepository.save(workOrder);
        log.info("Technician assigned to work order: {} by user: {}",
                workOrder.getWorkOrderNumber(), username);

        return mapToWorkOrderResponse(updatedWorkOrder);
    }

    @Transactional(readOnly = true)
    public List<WorkOrderResponse> getWorkOrdersByStatus(WorkOrderStatus status) {
        return workOrderRepository.findByStatus(status).stream()
                .map(this::mapToWorkOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<WorkOrderResponse> getWorkOrdersByTechnician(Long technicianId) {
        return workOrderRepository.findByAssignedTechnicianId(technicianId).stream()
                .map(this::mapToWorkOrderResponse)
                .collect(Collectors.toList());
    }

    // Helper method to generate work order number
    private String generateWorkOrderNumber() {
        YearMonth currentYearMonth = YearMonth.now();
        String baseNumber = "WO-" + currentYearMonth.getYear() + "-" +
                String.format("%02d", currentYearMonth.getMonthValue());

        long count = workOrderRepository.count() + 1;
        return baseNumber + "-" + String.format("%03d", count);
    }

    // Mapping helper
    private WorkOrderResponse mapToWorkOrderResponse(WorkOrder workOrder) {
        WorkOrderResponse response = new WorkOrderResponse();
        response.setId(workOrder.getId());
        response.setWorkOrderNumber(workOrder.getWorkOrderNumber());
        response.setTitle(workOrder.getTitle());
        response.setDescription(workOrder.getDescription());
        response.setStatus(workOrder.getStatus());
        response.setPriority(workOrder.getPriority());
        response.setMaintenanceType(workOrder.getMaintenanceType());
        response.setAircraftRegistration(workOrder.getAircraftRegistration());
        response.setAircraftType(workOrder.getAircraftType());
        response.setTailNumber(workOrder.getTailNumber());
        response.setEstimatedStartDate(workOrder.getEstimatedStartDate());
        response.setEstimatedCompletionDate(workOrder.getEstimatedCompletionDate());
        response.setActualStartDate(workOrder.getActualStartDate());
        response.setActualCompletionDate(workOrder.getActualCompletionDate());
        response.setEstimatedCost(workOrder.getEstimatedCost());
        response.setActualCost(workOrder.getActualCost());
        response.setCreatedAt(workOrder.getCreatedAt());
        response.setUpdatedAt(workOrder.getUpdatedAt());

        // Map assigned personnel
        if (workOrder.getCreatedBy() != null) {
            response.setCreatedBy(workOrder.getCreatedBy().getUsername());
        }
        if (workOrder.getAssignedTechnician() != null) {
            response.setAssignedTechnician(workOrder.getAssignedTechnician().getUsername());
        }
        if (workOrder.getApprovedBy() != null) {
            response.setApprovedBy(workOrder.getApprovedBy().getUsername());
        }

        // Map required parts
        if (workOrder.getRequiredParts() != null) {
            List<WorkOrderResponse.PartInfo> partInfos = workOrder.getRequiredParts().stream()
                    .map(part -> {
                        WorkOrderResponse.PartInfo partInfo = new WorkOrderResponse.PartInfo();
                        partInfo.setId(part.getId());
                        partInfo.setPartNumber(part.getPartNumber());
                        partInfo.setDescription(part.getDescription());
                        partInfo.setStatus(part.getServiceabilityStatus().toString());
                        return partInfo;
                    })
                    .collect(Collectors.toList());
            response.setRequiredParts(partInfos);
        }

        return response;
    }
}
