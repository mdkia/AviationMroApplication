package com.aviation.mro.modules.quality.repository;

import com.aviation.mro.modules.quality.domain.model.QualityCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QualityCheckRepository extends JpaRepository<QualityCheck, Long> {

    List<QualityCheck> findByInspectionPlanId(Long inspectionPlanId);

    List<QualityCheck> findByIsCriticalTrue();
}