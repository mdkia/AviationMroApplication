package com.aviation.mro.modules.sales.controller;

import com.aviation.mro.modules.sales.domain.dto.InvoiceResponse;
import com.aviation.mro.modules.sales.domain.enums.InvoiceStatus;
import com.aviation.mro.modules.sales.domain.enums.PaymentStatus;
import com.aviation.mro.modules.sales.domain.enums.PaymentMethod;
import com.aviation.mro.modules.sales.service.InvoiceService;
import com.aviation.mro.shared.common.ApiResponse;
import com.aviation.mro.shared.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sales/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoice Management", description = "APIs for managing invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/from-order/{salesOrderId}")
    @Operation(summary = "Create invoice from sales order")
    public ResponseEntity<ApiResponse> createInvoiceFromSalesOrder(
            @PathVariable Long salesOrderId,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        InvoiceResponse response = invoiceService.createInvoiceFromSalesOrder(salesOrderId, username);
        return ResponseEntity.ok(ApiResponse.success("Invoice created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all invoices")
    public ResponseEntity<ApiResponse> getAllInvoices() {
        List<InvoiceResponse> invoices = invoiceService.getAllInvoices();
        return ResponseEntity.ok(ApiResponse.success("Invoices retrieved successfully", invoices));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get invoice by ID")
    public ResponseEntity<ApiResponse> getInvoiceById(@PathVariable Long id) {
        InvoiceResponse invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(ApiResponse.success("Invoice retrieved successfully", invoice));
    }

    @GetMapping("/number/{invoiceNumber}")
    @Operation(summary = "Get invoice by number")
    public ResponseEntity<ApiResponse> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        InvoiceResponse invoice = invoiceService.getInvoiceByNumber(invoiceNumber);
        return ResponseEntity.ok(ApiResponse.success("Invoice retrieved successfully", invoice));
    }

    @PutMapping("/{id}/payment-status")
    @Operation(summary = "Update payment status")
    public ResponseEntity<ApiResponse> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam PaymentStatus paymentStatus,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        InvoiceResponse invoice = invoiceService.updatePaymentStatus(id, paymentStatus, username);
        return ResponseEntity.ok(ApiResponse.success("Payment status updated successfully", invoice));
    }

    @PutMapping("/{id}/record-payment")
    @Operation(summary = "Record payment")
    public ResponseEntity<ApiResponse> recordPayment(
            @PathVariable Long id,
            @RequestParam Double amount,
            @RequestParam PaymentMethod paymentMethod,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        InvoiceResponse invoice = invoiceService.recordPayment(id, amount, paymentMethod, username);
        return ResponseEntity.ok(ApiResponse.success("Payment recorded successfully", invoice));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get invoices by customer")
    public ResponseEntity<ApiResponse> getInvoicesByCustomer(@PathVariable Long customerId) {
        List<InvoiceResponse> invoices = invoiceService.getInvoicesByCustomer(customerId);
        return ResponseEntity.ok(ApiResponse.success("Invoices retrieved successfully", invoices));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get invoices by status")
    public ResponseEntity<ApiResponse> getInvoicesByStatus(@PathVariable InvoiceStatus status) {
        List<InvoiceResponse> invoices = invoiceService.getInvoicesByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Invoices retrieved successfully", invoices));
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue invoices")
    public ResponseEntity<ApiResponse> getOverdueInvoices() {
        List<InvoiceResponse> invoices = invoiceService.getOverdueInvoices();
        return ResponseEntity.ok(ApiResponse.success("Overdue invoices retrieved successfully", invoices));
    }
}
