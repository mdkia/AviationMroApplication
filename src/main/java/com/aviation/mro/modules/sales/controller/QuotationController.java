package com.aviation.mro.modules.sales.controller;

import com.aviation.mro.modules.sales.domain.dto.QuotationRequest;
import com.aviation.mro.modules.sales.domain.dto.QuotationResponse;
import com.aviation.mro.modules.sales.domain.enums.QuotationStatus;
import com.aviation.mro.modules.sales.service.QuotationService;
import com.aviation.mro.shared.common.ApiResponse;
import com.aviation.mro.shared.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sales/quotations")
@RequiredArgsConstructor
@Tag(name = "Quotation Management", description = "APIs for managing quotations")
public class QuotationController {

    private final QuotationService quotationService;

    @PostMapping
    @Operation(summary = "Create a new quotation")
    public ResponseEntity<ApiResponse> createQuotation(
            @Valid @RequestBody QuotationRequest request,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        QuotationResponse response = quotationService.createQuotation(request, username);
        return ResponseEntity.ok(ApiResponse.success("Quotation created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all quotations")
    public ResponseEntity<ApiResponse> getAllQuotations() {
        List<QuotationResponse> quotations = quotationService.getAllQuotations();
        return ResponseEntity.ok(ApiResponse.success("Quotations retrieved successfully", quotations));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get quotation by ID")
    public ResponseEntity<ApiResponse> getQuotationById(@PathVariable Long id) {
        QuotationResponse quotation = quotationService.getQuotationById(id);
        return ResponseEntity.ok(ApiResponse.success("Quotation retrieved successfully", quotation));
    }

    @GetMapping("/number/{quotationNumber}")
    @Operation(summary = "Get quotation by number")
    public ResponseEntity<ApiResponse> getQuotationByNumber(@PathVariable String quotationNumber) {
        QuotationResponse quotation = quotationService.getQuotationByNumber(quotationNumber);
        return ResponseEntity.ok(ApiResponse.success("Quotation retrieved successfully", quotation));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update quotation status")
    public ResponseEntity<ApiResponse> updateQuotationStatus(
            @PathVariable Long id,
            @RequestParam QuotationStatus status,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        QuotationResponse quotation = quotationService.updateQuotationStatus(id, status, username);
        return ResponseEntity.ok(ApiResponse.success("Quotation status updated successfully", quotation));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get quotations by customer")
    public ResponseEntity<ApiResponse> getQuotationsByCustomer(@PathVariable Long customerId) {
        List<QuotationResponse> quotations = quotationService.getQuotationsByCustomer(customerId);
        return ResponseEntity.ok(ApiResponse.success("Quotations retrieved successfully", quotations));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get quotations by status")
    public ResponseEntity<ApiResponse> getQuotationsByStatus(@PathVariable QuotationStatus status) {
        List<QuotationResponse> quotations = quotationService.getQuotationsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Quotations retrieved successfully", quotations));
    }

    @PutMapping("/{id}/convert-to-order")
    @Operation(summary = "Convert quotation to sales order")
    public ResponseEntity<ApiResponse> convertToSalesOrder(
            @PathVariable Long id,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        QuotationResponse quotation = quotationService.convertToSalesOrder(id, username);
        return ResponseEntity.ok(ApiResponse.success("Quotation converted to sales order successfully", quotation));
    }
}
