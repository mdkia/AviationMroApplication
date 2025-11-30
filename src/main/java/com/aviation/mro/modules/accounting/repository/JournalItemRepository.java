package com.aviation.mro.modules.accounting.repository;

import com.aviation.mro.modules.accounting.domain.model.JournalItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JournalItemRepository extends JpaRepository<JournalItem, Long> {

    List<JournalItem> findByJournalEntryId(Long journalEntryId);

    List<JournalItem> findByAccountId(Long accountId);

    @Query("SELECT ji FROM JournalItem ji WHERE ji.journalEntry.entryDate BETWEEN :startDate AND :endDate AND ji.journalEntry.isPosted = true")
    List<JournalItem> findPostedItemsBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(ji.debitAmount), 0) FROM JournalItem ji WHERE ji.account.id = :accountId AND ji.journalEntry.entryDate BETWEEN :startDate AND :endDate AND ji.journalEntry.isPosted = true")
    Double getAccountDebitTotal(Long accountId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(ji.creditAmount), 0) FROM JournalItem ji WHERE ji.account.id = :accountId AND ji.journalEntry.entryDate BETWEEN :startDate AND :endDate AND ji.journalEntry.isPosted = true")
    Double getAccountCreditTotal(Long accountId, LocalDateTime startDate, LocalDateTime endDate);
}
