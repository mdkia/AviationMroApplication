package com.aviation.mro.modules.sales.repository;

import com.aviation.mro.modules.sales.domain.model.Quotation;
import com.aviation.mro.modules.sales.domain.enums.QuotationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long> {

    Optional<Quotation> findByQuotationNumber(String quotationNumber);

    List<Quotation> findByCustomerId(Long customerId);

    List<Quotation> findByStatus(QuotationStatus status);

    List<Quotation> findByCreatedById(Long userId);

    @Query("SELECT q FROM Quotation q WHERE q.expiryDate < :currentDate AND q.status = 'SENT'")
    List<Quotation> findExpiredQuotations(LocalDateTime currentDate);

    @Query("SELECT q FROM Quotation q WHERE q.quotationDate BETWEEN :startDate AND :endDate")
    List<Quotation> findByQuotationDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT SUM(q.totalAmount) FROM Quotation q WHERE q.status = 'ACCEPTED' AND q.quotationDate BETWEEN :startDate AND :endDate")
    Double getTotalAcceptedAmount(LocalDateTime startDate, LocalDateTime endDate);

    boolean existsByQuotationNumber(String quotationNumber);
}
