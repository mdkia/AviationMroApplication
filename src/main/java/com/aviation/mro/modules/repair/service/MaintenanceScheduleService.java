package com.aviation.mro.modules.repair.service;


import com.aviation.mro.modules.repair.domain.dto.MaintenanceScheduleRequest;
import com.aviation.mro.modules.repair.domain.dto.MaintenanceScheduleResponse;
import com.aviation.mro.modules.repair.domain.model.MaintenanceSchedule;
import com.aviation.mro.modules.repair.domain.model.WorkOrder;
import com.aviation.mro.modules.repair.repository.MaintenanceScheduleRepository;
import com.aviation.mro.modules.repair.repository.WorkOrderRepository;
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
public class MaintenanceScheduleService {

    private final MaintenanceScheduleRepository maintenanceScheduleRepository;
    private final WorkOrderRepository workOrderRepository;

    @Transactional
    public MaintenanceScheduleResponse createSchedule(MaintenanceScheduleRequest request, String username) {
        MaintenanceSchedule schedule = new MaintenanceSchedule();
        schedule.setScheduleNumber(generateScheduleNumber());
        schedule.setAircraftRegistration(request.getAircraftRegistration());
        schedule.setAircraftType(request.getAircraftType());
        schedule.setMaintenanceType(request.getMaintenanceType());
        schedule.setScheduledStartDate(request.getScheduledStartDate());
        schedule.setScheduledEndDate(request.getScheduledEndDate());
        schedule.setFlightHours(request.getFlightHours());
        schedule.setFlightCycles(request.getFlightCycles());
        schedule.setDaysSinceLastCheck(request.getDaysSinceLastCheck());
        schedule.setCreatedByUser(username);

        MaintenanceSchedule savedSchedule = maintenanceScheduleRepository.save(schedule);
        log.info("Maintenance schedule created: {} for aircraft: {}",
                savedSchedule.getScheduleNumber(), request.getAircraftRegistration());

        return mapToMaintenanceScheduleResponse(savedSchedule);
    }

    @Transactional
    public MaintenanceScheduleResponse linkToWorkOrder(Long scheduleId, Long workOrderId, String username) {
        MaintenanceSchedule schedule = maintenanceScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("Maintenance schedule not found with id: " + scheduleId));

        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new NotFoundException("Work order not found with id: " + workOrderId));

        schedule.setWorkOrder(workOrder);
        schedule.setUpdatedByUser(username);

        MaintenanceSchedule updatedSchedule = maintenanceScheduleRepository.save(schedule);
        log.info("Maintenance schedule {} linked to work order {}",
                schedule.getScheduleNumber(), workOrder.getWorkOrderNumber());

        return mapToMaintenanceScheduleResponse(updatedSchedule);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceScheduleResponse> getSchedulesByAircraft(String aircraftRegistration) {
        return maintenanceScheduleRepository.findByAircraftRegistration(aircraftRegistration).stream()
                .map(this::mapToMaintenanceScheduleResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MaintenanceScheduleResponse> getPendingSchedules() {
        return maintenanceScheduleRepository.findByIsCompletedFalse().stream()
                .map(this::mapToMaintenanceScheduleResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MaintenanceScheduleResponse> getOverdueSchedules() {
        return maintenanceScheduleRepository.findOverdueSchedules(LocalDateTime.now()).stream()
                .map(this::mapToMaintenanceScheduleResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MaintenanceScheduleResponse markAsCompleted(Long scheduleId, String username) {
        MaintenanceSchedule schedule = maintenanceScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("Maintenance schedule not found with id: " + scheduleId));

        schedule.setIsCompleted(true);
        schedule.setActualEndDate(LocalDateTime.now());
        schedule.setUpdatedByUser(username);

        MaintenanceSchedule updatedSchedule = maintenanceScheduleRepository.save(schedule);
        log.info("Maintenance schedule marked as completed: {}", schedule.getScheduleNumber());

        return mapToMaintenanceScheduleResponse(updatedSchedule);
    }

    private String generateScheduleNumber() {
        return "SCH-" + LocalDateTime.now().getYear() + "-" +
                String.format("%03d", maintenanceScheduleRepository.count() + 1);
    }

    private MaintenanceScheduleResponse mapToMaintenanceScheduleResponse(MaintenanceSchedule schedule) {
        MaintenanceScheduleResponse response = new MaintenanceScheduleResponse();
        response.setId(schedule.getId());
        response.setScheduleNumber(schedule.getScheduleNumber());
        response.setAircraftRegistration(schedule.getAircraftRegistration());
        response.setAircraftType(schedule.getAircraftType());
        response.setMaintenanceType(schedule.getMaintenanceType());
        response.setScheduledStartDate(schedule.getScheduledStartDate());
        response.setScheduledEndDate(schedule.getScheduledEndDate());
        response.setActualStartDate(schedule.getActualStartDate());
        response.setActualEndDate(schedule.getActualEndDate());
        response.setFlightHours(schedule.getFlightHours());
        response.setFlightCycles(schedule.getFlightCycles());
        response.setDaysSinceLastCheck(schedule.getDaysSinceLastCheck());
        response.setIsCompleted(schedule.getIsCompleted());
        response.setIsOverdue(schedule.getIsOverdue());
        response.setCreatedAt(schedule.getCreatedAt());
        response.setUpdatedAt(schedule.getUpdatedAt());

        if (schedule.getWorkOrder() != null) {
            response.setWorkOrderId(schedule.getWorkOrder().getId());
            response.setWorkOrderNumber(schedule.getWorkOrder().getWorkOrderNumber());
        }

        return response;
    }
}
