// SalesReportHelper.java
package com.aviation.mro.modules.reporting.service;

import com.aviation.mro.modules.auth.model.User;
import com.aviation.mro.modules.sales.domain.enums.PaymentStatus;
import com.aviation.mro.modules.sales.domain.model.Invoice;
import com.aviation.mro.modules.sales.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SalesReportHelper {

    private final InvoiceRepository invoiceRepository;

    public long countUnpaidInvoices() {
        return invoiceRepository.findAll().stream()
                .filter(inv -> inv.getPaymentStatus() != PaymentStatus.PAID)
                .count();
    }

    public long countPaidInvoices() {
        return invoiceRepository.findAll().stream()
                .filter(inv -> inv.getPaymentStatus() == PaymentStatus.PAID)
                .count();
    }

    public Map<String, Object> getSalesPerformance(LocalDateTime start, LocalDateTime end) {
        // تعریف متغیرهای effectively final
        LocalDateTime effectiveStart = start != null ? start :
                LocalDateTime.now().minusMonths(1).withDayOfMonth(1)
                        .withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime effectiveEnd = end != null ? end : LocalDateTime.now();

        List<Invoice> invoices = invoiceRepository.findAll().stream()
                .filter(inv -> inv.getInvoiceDate() != null)
                .filter(inv -> !inv.getInvoiceDate().isBefore(effectiveStart) &&
                        inv.getInvoiceDate().isBefore(effectiveEnd))
                .toList();

        BigDecimal totalSales = invoices.stream()
                .filter(inv -> inv.getPaymentStatus() == PaymentStatus.PAID)
                .map(inv -> BigDecimal.valueOf(inv.getTotalAmount() != null ? inv.getTotalAmount() : 0.0))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalInvoices = invoices.size();
        long paidInvoices = invoices.stream()
                .filter(i -> i.getPaymentStatus() == PaymentStatus.PAID)
                .count();
        long partiallyPaid = invoices.stream()
                .filter(i -> i.getPaymentStatus() == PaymentStatus.PARTIALLY_PAID)
                .count();
        long unpaidInvoices = totalInvoices - paidInvoices - partiallyPaid;

        BigDecimal totalOutstanding = invoices.stream()
                .filter(inv -> inv.getBalanceDue() != null && inv.getBalanceDue() > 0)
                .map(inv -> BigDecimal.valueOf(inv.getBalanceDue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageInvoiceValue = totalInvoices > 0
                ? totalSales.divide(BigDecimal.valueOf(totalInvoices), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return Map.ofEntries(
                Map.entry("periodStart", effectiveStart),
                Map.entry("periodEnd", effectiveEnd),
                Map.entry("totalInvoices", totalInvoices),
                Map.entry("paidInvoices", paidInvoices),
                Map.entry("partiallyPaidInvoices", partiallyPaid),
                Map.entry("unpaidInvoices", unpaidInvoices),
                Map.entry("totalSalesAmount", totalSales),
                Map.entry("totalOutstandingAmount", totalOutstanding),
                Map.entry("averageInvoiceValue", averageInvoiceValue),
                Map.entry("generatedAt", LocalDateTime.now())
        );
    }

    public List<Map<String, Object>> getInvoiceDetailsReport(
            LocalDateTime start,
            LocalDateTime end,
            String paymentStatus) {

        // استفاده از متغیرهای effectively final
        LocalDateTime effectiveStart = start != null ? start :
                LocalDateTime.now().minusMonths(1).withDayOfMonth(1)
                        .withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime effectiveEnd = end != null ? end : LocalDateTime.now();

        List<Invoice> invoices = invoiceRepository.findAll();

        return invoices.stream()
                .filter(inv -> effectiveStart == null ||
                        (inv.getInvoiceDate() != null && !inv.getInvoiceDate().isBefore(effectiveStart)))
                .filter(inv -> effectiveEnd == null ||
                        (inv.getInvoiceDate() != null && inv.getInvoiceDate().isBefore(effectiveEnd)))
                .filter(inv -> paymentStatus == null ||
                        (inv.getPaymentStatus() != null &&
                                inv.getPaymentStatus().name().equalsIgnoreCase(paymentStatus.trim())))
                .map(inv -> Map.<String, Object>ofEntries(
                        Map.entry("invoiceId", inv.getId()),
                        Map.entry("invoiceNumber", safeString(inv.getInvoiceNumber())),
                        Map.entry("salesOrderId", inv.getSalesOrder() != null ? inv.getSalesOrder().getId() : null),
                        Map.entry("invoiceDate", inv.getInvoiceDate()),
                        Map.entry("dueDate", inv.getDueDate()),
                        Map.entry("totalAmount", inv.getTotalAmount() != null ? inv.getTotalAmount() : 0.0),
                        Map.entry("amountPaid", inv.getAmountPaid() != null ? inv.getAmountPaid() : 0.0),
                        Map.entry("balanceDue", inv.getBalanceDue() != null ? inv.getBalanceDue() : 0.0),
                        Map.entry("paymentStatus", inv.getPaymentStatus() != null ? inv.getPaymentStatus().name() : "نامشخص"),
                        Map.entry("paymentStatusInPersian", getPersianPaymentStatus(inv.getPaymentStatus())),
                        Map.entry("invoiceStatus", inv.getInvoiceStatus() != null ? inv.getInvoiceStatus().name() : ""),
                        Map.entry("createdBy", getFullName(inv.getCreatedBy())),
                        Map.entry("isOverdue", inv.getDueDate() != null &&
                                inv.getDueDate().isBefore(LocalDateTime.now()) &&
                                inv.getPaymentStatus() != PaymentStatus.PAID)
                ))
                .toList();
    }

    private String getPersianPaymentStatus(PaymentStatus status) {
        if (status == null) return "نامشخص";
        return switch (status) {
            case PENDING -> "در انتظار پرداخت";
            case PARTIALLY_PAID -> "پرداخت جزئی";
            case PAID -> "پرداخت شده";
            case OVERDUE -> "سررسید گذشته";
            case CANCELLED -> "لغو شده";
            default -> status.name();
        };
    }

    private String getFullName(User user) {
        if (user == null) return "نامشخص";
        String first = user.getFirstName() != null ? user.getFirstName().trim() : "";
        String last = user.getLastName() != null ? user.getLastName().trim() : "";
        if (first.isEmpty() && last.isEmpty()) return "نامشخص";
        return (first + " " + last).trim();
    }

    private String safeString(String value) {
        return value != null ? value : "";
    }
}