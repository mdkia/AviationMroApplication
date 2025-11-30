package com.aviation.mro.modules.quality.repository;

import com.aviation.mro.modules.quality.domain.model.InspectionPlan;
import com.aviation.mro.modules.quality.domain.enums.InspectionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InspectionPlanRepository extends JpaRepository<InspectionPlan, Long> {

    Optional<InspectionPlan> findByPlanNumber(String planNumber);

    List<InspectionPlan> findByInspectionType(InspectionType inspectionType);

    List<InspectionPlan> findByIsActiveTrue();

    List<InspectionPlan> findByApplicableStandardsContaining(String standard);

    @Query("SELECT ip FROM InspectionPlan ip WHERE ip.isActive = true AND ip.inspectionType = :inspectionType")
    List<InspectionPlan> findActivePlansByType(InspectionType inspectionType);

    boolean existsByPlanNumber(String planNumber);
}
