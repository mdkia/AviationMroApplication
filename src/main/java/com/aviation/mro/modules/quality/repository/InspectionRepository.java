package com.aviation.mro.modules.quality.repository;
import com.aviation.mro.modules.quality.domain.model.Inspection;
import com.aviation.mro.modules.quality.domain.enums.InspectionStatus;
import com.aviation.mro.modules.quality.domain.enums.ComplianceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Long> {

    Optional<Inspection> findByInspectionNumber(String inspectionNumber);

    List<Inspection> findByStatus(InspectionStatus status);

    List<Inspection> findByComplianceStatus(ComplianceStatus complianceStatus);

    List<Inspection> findByInspectorId(Long inspectorId);

    List<Inspection> findByPartId(Long partId);

    List<Inspection> findByWorkOrderId(Long workOrderId);

    @Query("SELECT i FROM Inspection i WHERE i.scheduledDate BETWEEN :startDate AND :endDate")
    List<Inspection> findScheduledInspections(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT i FROM Inspection i WHERE i.actualStartDate IS NOT NULL AND i.actualEndDate IS NULL")
    List<Inspection> findInProgressInspections();

    @Query("SELECT COUNT(i) FROM Inspection i WHERE i.status = :status")
    Long countByStatus(InspectionStatus status);

    boolean existsByInspectionNumber(String inspectionNumber);
}