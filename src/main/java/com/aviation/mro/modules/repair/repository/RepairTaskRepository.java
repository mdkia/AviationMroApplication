package com.aviation.mro.modules.repair.repository;

import com.aviation.mro.modules.repair.domain.model.RepairTask;
import com.aviation.mro.modules.repair.domain.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairTaskRepository extends JpaRepository<RepairTask, Long> {

    List<RepairTask> findByWorkOrderId(Long workOrderId);

    List<RepairTask> findByAssignedTechnicianId(Long technicianId);

    List<RepairTask> findByStatus(TaskStatus status);

    List<RepairTask> findByTaskCode(String taskCode);
}
