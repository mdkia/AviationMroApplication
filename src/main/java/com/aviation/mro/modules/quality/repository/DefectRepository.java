package com.aviation.mro.modules.quality.repository;
import com.aviation.mro.modules.quality.domain.model.Defect;
import com.aviation.mro.modules.quality.domain.enums.ComplianceStatus;
import com.aviation.mro.modules.quality.domain.enums.DefectSeverity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DefectRepository extends JpaRepository<Defect, Long> {

    List<Defect> findByInspectionId(Long inspectionId);

    List<Defect> findByComplianceStatus(ComplianceStatus complianceStatus);

    List<Defect> findBySeverity(DefectSeverity severity);

    @Query("SELECT d FROM Defect d WHERE d.inspection.part.id = :partId")
    List<Defect> findByPartId(Long partId);

    @Query("SELECT COUNT(d) FROM Defect d WHERE d.complianceStatus = 'NON_COMPLIANT' AND d.severity = 'CRITICAL'")
    Long countCriticalDefects();
}