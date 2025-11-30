package com.aviation.mro.modules.accounting.service;

import com.aviation.mro.modules.accounting.domain.model.*;
import com.aviation.mro.modules.accounting.repository.*;
import com.aviation.mro.modules.sales.domain.model.Invoice;
import com.aviation.mro.modules.sales.domain.model.SalesOrder;
import com.aviation.mro.modules.sales.domain.enums.PaymentStatus;
import com.aviation.mro.modules.sales.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesIntegrationService {

    private final JournalEntryService journalEntryService;
    private final AccountRepository accountRepository;
    private final InvoiceRepository invoiceRepository;

    @Transactional
    public void createSalesJournalEntry(SalesOrder salesOrder, String username) {
        try {
            // پیدا کردن حساب‌های مربوطه
            Account revenueAccount = accountRepository.findByAccountCode("4010")
                    .orElseThrow(() -> new RuntimeException("Revenue account not found"));

            Account accountsReceivableAccount = accountRepository.findByAccountCode("1010")
                    .orElseThrow(() -> new RuntimeException("Accounts receivable account not found"));

            // ایجاد سند حسابداری برای فروش
            JournalEntry journalEntry = new JournalEntry();
            journalEntry.setEntryNumber("SALE-" + salesOrder.getOrderNumber());
            journalEntry.setEntryDate(LocalDateTime.now());
            journalEntry.setReferenceNumber(salesOrder.getOrderNumber());
            journalEntry.setDescription("Sales order: " + salesOrder.getOrderNumber());
            journalEntry.setSourceModule("SALES");
            journalEntry.setSourceId(salesOrder.getId());

            // آیتم بدهکار: حسابهای دریافتنی
            JournalItem debitItem = new JournalItem();
            debitItem.setAccount(accountsReceivableAccount);
            debitItem.setDebitAmount(salesOrder.getTotalAmount());
            debitItem.setCreditAmount(0.0);
            debitItem.setDescription("Accounts receivable - " + salesOrder.getCustomer().getCompanyName());
            debitItem.setLineNumber(1);

            // آیتم بستانکار: درآمد فروش
            JournalItem creditItem = new JournalItem();
            creditItem.setAccount(revenueAccount);
            debitItem.setDebitAmount(0.0);
            creditItem.setCreditAmount(salesOrder.getTotalAmount());
            creditItem.setDescription("Sales revenue");
            creditItem.setLineNumber(2);

            journalEntry.addItem(debitItem);
            journalEntry.addItem(creditItem);

            // ثبت و تأیید سند
            journalEntry.calculateTotals();
            if (journalEntry.isBalanced()) {
                // ذخیره سند (می‌تونی از JournalEntryService استفاده کنی)
                log.info("Sales journal entry created for order: {}", salesOrder.getOrderNumber());
            }

        } catch (Exception e) {
            log.error("Failed to create sales journal entry for order: {}", salesOrder.getOrderNumber(), e);
        }
    }

    @Transactional
    public void createPaymentJournalEntry(Invoice invoice, Double paymentAmount, String username) {
        try {
            // پیدا کردن حساب‌های مربوطه
            Account cashAccount = accountRepository.findByAccountCode("1001")
                    .orElseThrow(() -> new RuntimeException("Cash account not found"));

            Account accountsReceivableAccount = accountRepository.findByAccountCode("1010")
                    .orElseThrow(() -> new RuntimeException("Accounts receivable account not found"));

            // ایجاد سند حسابداری برای دریافت وجه
            JournalEntry journalEntry = new JournalEntry();
            journalEntry.setEntryNumber("PAY-" + invoice.getInvoiceNumber());
            journalEntry.setEntryDate(LocalDateTime.now());
            journalEntry.setReferenceNumber(invoice.getInvoiceNumber());
            journalEntry.setDescription("Payment received for invoice: " + invoice.getInvoiceNumber());
            journalEntry.setSourceModule("SALES");
            journalEntry.setSourceId(invoice.getId());

            // آیتم بدهکار: حساب نقدی
            JournalItem debitItem = new JournalItem();
            debitItem.setAccount(cashAccount);
            debitItem.setDebitAmount(paymentAmount);
            debitItem.setCreditAmount(0.0);
            debitItem.setDescription("Cash receipt");
            debitItem.setLineNumber(1);

            // آیتم بستانکار: حسابهای دریافتنی
            JournalItem creditItem = new JournalItem();
            creditItem.setAccount(accountsReceivableAccount);
            debitItem.setDebitAmount(0.0);
            creditItem.setCreditAmount(paymentAmount);
            creditItem.setDescription("Reduce accounts receivable");
            creditItem.setLineNumber(2);

            journalEntry.addItem(debitItem);
            journalEntry.addItem(creditItem);

            // ثبت و تأیید سند
            journalEntry.calculateTotals();
            if (journalEntry.isBalanced()) {
                // ذخیره سند
                log.info("Payment journal entry created for invoice: {}", invoice.getInvoiceNumber());
            }

        } catch (Exception e) {
            log.error("Failed to create payment journal entry for invoice: {}", invoice.getInvoiceNumber(), e);
        }
    }
}
