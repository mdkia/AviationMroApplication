package com.aviation.mro.modules.repair.repository;


import com.aviation.mro.modules.repair.domain.model.WorkOrder;
import com.aviation.mro.modules.repair.domain.enums.WorkOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    Optional<WorkOrder> findByWorkOrderNumber(String workOrderNumber);

    List<WorkOrder> findByStatus(WorkOrderStatus status);

    List<WorkOrder> findByPriority(String priority);

    List<WorkOrder> findByAircraftRegistration(String aircraftRegistration);

    List<WorkOrder> findByAssignedTechnicianId(Long technicianId);

    @Query("SELECT wo FROM WorkOrder wo WHERE wo.estimatedCompletionDate < :currentDate AND wo.status NOT IN :completedStatuses")
    List<WorkOrder> findOverdueWorkOrders(LocalDateTime currentDate, List<WorkOrderStatus> completedStatuses);

    @Query("SELECT COUNT(wo) FROM WorkOrder wo WHERE wo.status = :status")
    Long countByStatus(WorkOrderStatus status);

    boolean existsByWorkOrderNumber(String workOrderNumber);
}