package com.aviation.mro.modules.repair.repository;

import com.aviation.mro.modules.repair.domain.model.MaintenanceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceSchedule, Long> {

    List<MaintenanceSchedule> findByAircraftRegistration(String aircraftRegistration);

    List<MaintenanceSchedule> findByIsCompletedFalse();

    @Query("SELECT ms FROM MaintenanceSchedule ms WHERE ms.scheduledStartDate BETWEEN :startDate AND :endDate")
    List<MaintenanceSchedule> findScheduledBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT ms FROM MaintenanceSchedule ms WHERE ms.scheduledEndDate < :currentDate AND ms.isCompleted = false")
    List<MaintenanceSchedule> findOverdueSchedules(LocalDateTime currentDate);
}
