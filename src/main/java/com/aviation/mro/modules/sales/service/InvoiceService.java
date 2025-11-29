package com.aviation.mro.modules.sales.service;

import com.aviation.mro.modules.sales.domain.dto.InvoiceResponse;
import com.aviation.mro.modules.sales.domain.model.*;
import com.aviation.mro.modules.sales.domain.enums.InvoiceStatus;
import com.aviation.mro.modules.sales.domain.enums.PaymentStatus;
import com.aviation.mro.modules.sales.domain.enums.PaymentMethod;
import com.aviation.mro.modules.sales.repository.*;
import com.aviation.mro.modules.auth.model.User;
import com.aviation.mro.modules.auth.repository.UserRepository;
import com.aviation.mro.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final UserRepository userRepository;

    @Transactional
    public InvoiceResponse createInvoiceFromSalesOrder(Long salesOrderId, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        SalesOrder salesOrder = salesOrderRepository.findById(salesOrderId)
                .orElseThrow(() -> new NotFoundException("Sales order not found with id: " + salesOrderId));

        // Check if invoice already exists for this sales order
        if (salesOrder.getInvoice() != null) {
            throw new IllegalStateException("Invoice already exists for sales order: " + salesOrder.getOrderNumber());
        }

        // Generate invoice number
        String invoiceNumber = generateInvoiceNumber();

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setSalesOrder(salesOrder);
        invoice.setInvoiceStatus(InvoiceStatus.ISSUED);
        invoice.setPaymentStatus(PaymentStatus.PENDING);
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setDueDate(LocalDateTime.now().plusDays(30)); // 30 days due date
        invoice.setSubtotal(salesOrder.getSubtotal());
        invoice.setTaxAmount(salesOrder.getTaxAmount());
        invoice.setTotalAmount(salesOrder.getTotalAmount());
        invoice.setAmountPaid(0.0);
        invoice.setCreatedBy(currentUser);

        // Calculate balance
        invoice.calculateBalance();

        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice created: {} for sales order: {} by user: {}",
                invoiceNumber, salesOrder.getOrderNumber(), username);

        return mapToInvoiceResponse(savedInvoice);
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(this::mapToInvoiceResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Invoice not found with id: " + id));
        return mapToInvoiceResponse(invoice);
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceByNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new NotFoundException("Invoice not found: " + invoiceNumber));
        return mapToInvoiceResponse(invoice);
    }

    @Transactional
    public InvoiceResponse updatePaymentStatus(Long invoiceId, PaymentStatus paymentStatus, String username) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new NotFoundException("Invoice not found with id: " + invoiceId));

        invoice.setPaymentStatus(paymentStatus);

        if (paymentStatus == PaymentStatus.PAID) {
            invoice.setPaymentDate(LocalDateTime.now());
            invoice.setAmountPaid(invoice.getTotalAmount());
        }

        invoice.calculateBalance();

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice payment status updated: {} to {} by user: {}",
                invoice.getInvoiceNumber(), paymentStatus, username);

        return mapToInvoiceResponse(updatedInvoice);
    }

    @Transactional
    public InvoiceResponse recordPayment(Long invoiceId, Double amount, PaymentMethod paymentMethod, String username) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new NotFoundException("Invoice not found with id: " + invoiceId));

        invoice.setAmountPaid(invoice.getAmountPaid() != null ? invoice.getAmountPaid() + amount : amount);
        invoice.setPaymentMethod(paymentMethod);
        invoice.setPaymentDate(LocalDateTime.now());

        // Calculate balance and update payment status
        invoice.calculateBalance();

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        log.info("Payment recorded for invoice: {} - Amount: {} by user: {}",
                invoice.getInvoiceNumber(), amount, username);

        return mapToInvoiceResponse(updatedInvoice);
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesByCustomer(Long customerId) {
        return invoiceRepository.findBySalesOrderCustomerId(customerId).stream()
                .map(this::mapToInvoiceResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesByStatus(InvoiceStatus status) {
        return invoiceRepository.findByInvoiceStatus(status).stream()
                .map(this::mapToInvoiceResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getOverdueInvoices() {
        return invoiceRepository.findOverdueInvoices(LocalDateTime.now()).stream()
                .map(this::mapToInvoiceResponse)
                .collect(Collectors.toList());
    }

    // Helper method to generate invoice number
    private String generateInvoiceNumber() {
        LocalDateTime now = LocalDateTime.now();
        String baseNumber = "INV-" + now.getYear() + "-" + String.format("%02d", now.getMonthValue());

        long count = invoiceRepository.count() + 1;
        return baseNumber + "-" + String.format("%03d", count);
    }

    // Mapping helper
    private InvoiceResponse mapToInvoiceResponse(Invoice invoice) {
        InvoiceResponse response = new InvoiceResponse();
        response.setId(invoice.getId());
        response.setInvoiceNumber(invoice.getInvoiceNumber());
        response.setSalesOrderId(invoice.getSalesOrder().getId());
        response.setSalesOrderNumber(invoice.getSalesOrder().getOrderNumber());
        response.setCustomerId(invoice.getSalesOrder().getCustomer().getId());
        response.setCustomerName(invoice.getSalesOrder().getCustomer().getCompanyName());
        response.setInvoiceStatus(invoice.getInvoiceStatus());
        response.setPaymentStatus(invoice.getPaymentStatus());
        response.setPaymentMethod(invoice.getPaymentMethod());
        response.setInvoiceDate(invoice.getInvoiceDate());
        response.setDueDate(invoice.getDueDate());
        response.setPaymentDate(invoice.getPaymentDate());
        response.setSubtotal(invoice.getSubtotal());
        response.setTaxAmount(invoice.getTaxAmount());
        response.setTotalAmount(invoice.getTotalAmount());
        response.setAmountPaid(invoice.getAmountPaid());
        response.setBalanceDue(invoice.getBalanceDue());
        response.setCreatedAt(invoice.getCreatedAt());
        response.setUpdatedAt(invoice.getUpdatedAt());

        if (invoice.getCreatedBy() != null) {
            response.setCreatedBy(invoice.getCreatedBy().getUsername());
        }

        return response;
    }
}
