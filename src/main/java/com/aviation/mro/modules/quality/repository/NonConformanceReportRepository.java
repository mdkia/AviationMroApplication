package com.aviation.mro.modules.quality.repository;

import com.aviation.mro.modules.quality.domain.model.NonConformanceReport;
import com.aviation.mro.modules.quality.domain.enums.NCRStatus;
import com.aviation.mro.modules.quality.domain.enums.CorrectiveActionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NonConformanceReportRepository extends JpaRepository<NonConformanceReport, Long> {

    Optional<NonConformanceReport> findByNcrNumber(String ncrNumber);

    List<NonConformanceReport> findByStatus(NCRStatus status);

    List<NonConformanceReport> findByCorrectiveActionStatus(CorrectiveActionStatus correctiveActionStatus);

    List<NonConformanceReport> findByAssignedTo(String assignedTo);

    @Query("SELECT ncr FROM NonConformanceReport ncr WHERE ncr.targetCompletionDate < :currentDate AND ncr.status != 'CLOSED'")
    List<NonConformanceReport> findOverdueNCRs(LocalDateTime currentDate);

    @Query("SELECT COUNT(ncr) FROM NonConformanceReport ncr WHERE ncr.status = 'OPEN'")
    Long countOpenNCRs();

    boolean existsByNcrNumber(String ncrNumber);
}