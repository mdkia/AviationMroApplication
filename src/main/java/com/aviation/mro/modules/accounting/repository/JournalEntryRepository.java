package com.aviation.mro.modules.accounting.repository;


import com.aviation.mro.modules.accounting.domain.model.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {

    Optional<JournalEntry> findByEntryNumber(String entryNumber);

    List<JournalEntry> findByEntryDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<JournalEntry> findByIsPosted(Boolean isPosted);

    List<JournalEntry> findBySourceModuleAndSourceId(String sourceModule, Long sourceId);

    @Query("SELECT je FROM JournalEntry je WHERE je.entryDate BETWEEN :startDate AND :endDate AND je.isPosted = true")
    List<JournalEntry> findPostedEntriesBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(je.totalDebit), 0) FROM JournalEntry je WHERE je.entryDate BETWEEN :startDate AND :endDate AND je.isPosted = true")
    Double getTotalDebitBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(je.totalCredit), 0) FROM JournalEntry je WHERE je.entryDate BETWEEN :startDate AND :endDate AND je.isPosted = true")
    Double getTotalCreditBetween(LocalDateTime startDate, LocalDateTime endDate);

    boolean existsByEntryNumber(String entryNumber);
}
