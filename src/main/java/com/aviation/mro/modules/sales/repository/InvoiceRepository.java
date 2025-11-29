package com.aviation.mro.modules.sales.repository;

import com.aviation.mro.modules.sales.domain.model.Invoice;
import com.aviation.mro.modules.sales.domain.enums.InvoiceStatus;
import com.aviation.mro.modules.sales.domain.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    List<Invoice> findBySalesOrderCustomerId(Long customerId);

    List<Invoice> findByInvoiceStatus(InvoiceStatus status);

    List<Invoice> findByPaymentStatus(PaymentStatus paymentStatus);

    @Query("SELECT i FROM Invoice i WHERE i.dueDate < :currentDate AND i.paymentStatus NOT IN ('PAID', 'CANCELLED')")
    List<Invoice> findOverdueInvoices(LocalDateTime currentDate);

    @Query("SELECT i FROM Invoice i WHERE i.invoiceDate BETWEEN :startDate AND :endDate")
    List<Invoice> findByInvoiceDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.paymentStatus = 'PAID' AND i.invoiceDate BETWEEN :startDate AND :endDate")
    Double getTotalPaidAmount(LocalDateTime startDate, LocalDateTime endDate);

    boolean existsByInvoiceNumber(String invoiceNumber);
}
