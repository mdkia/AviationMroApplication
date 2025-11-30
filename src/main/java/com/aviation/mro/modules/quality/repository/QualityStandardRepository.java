package com.aviation.mro.modules.quality.repository;

import com.aviation.mro.modules.quality.domain.model.QualityStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface QualityStandardRepository extends JpaRepository<QualityStandard, Long> {

    Optional<QualityStandard> findByStandardCode(String standardCode);

    List<QualityStandard> findByIssuingAuthority(String issuingAuthority);

    List<QualityStandard> findByIsActiveTrue();

    @Query("SELECT qs FROM QualityStandard qs WHERE qs.effectiveDate <= CURRENT_DATE AND (qs.expiryDate IS NULL OR qs.expiryDate >= CURRENT_DATE)")
    List<QualityStandard> findActiveStandards();

    boolean existsByStandardCode(String standardCode);
}
