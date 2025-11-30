package com.aviation.mro.modules.accounting.repository;

import com.aviation.mro.modules.accounting.domain.model.FinancialPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialPeriodRepository extends JpaRepository<FinancialPeriod, Long> {

    Optional<FinancialPeriod> findByPeriodCode(String periodCode);

    Optional<FinancialPeriod> findByYearMonth(YearMonth yearMonth);

    List<FinancialPeriod> findByIsOpenTrue();

    List<FinancialPeriod> findByIsClosedTrue();

    @Query("SELECT fp FROM FinancialPeriod fp WHERE fp.startDate <= CURRENT_DATE AND fp.endDate >= CURRENT_DATE AND fp.isOpen = true")
    Optional<FinancialPeriod> findCurrentPeriod();

    boolean existsByPeriodCode(String periodCode);
}
