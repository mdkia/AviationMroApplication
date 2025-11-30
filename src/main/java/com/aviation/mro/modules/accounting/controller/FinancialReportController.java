package com.aviation.mro.modules.accounting.controller;

import com.aviation.mro.modules.accounting.domain.dto.FinancialReportResponse;
import com.aviation.mro.modules.accounting.domain.enums.FinancialStatementType;
import com.aviation.mro.modules.accounting.service.FinancialReportService;
import com.aviation.mro.shared.common.ApiResponse;
import com.aviation.mro.shared.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/accounting/financial-reports")
@RequiredArgsConstructor
@Tag(name = "Financial Reports", description = "APIs for generating financial statements")
public class FinancialReportController {

    private final FinancialReportService financialReportService;

    @GetMapping("/balance-sheet")
    @Operation(summary = "Generate balance sheet")
    public ResponseEntity<ApiResponse> generateBalanceSheet(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime asOfDate,
            Authentication authentication) {

        String username = SecurityUtils.getCurrentUsername(authentication);
        LocalDateTime targetDate = asOfDate != null ? asOfDate : LocalDateTime.now();

        FinancialReportResponse response = financialReportService.generateBalanceSheet(targetDate, username);
        return ResponseEntity.ok(ApiResponse.success("Balance sheet generated successfully", response));
    }

    @GetMapping("/income-statement")
    @Operation(summary = "Generate income statement")
    public ResponseEntity<ApiResponse> generateIncomeStatement(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Authentication authentication) {

        String username = SecurityUtils.getCurrentUsername(authentication);
        LocalDateTime periodStart = startDate != null ? startDate : LocalDateTime.now().minusMonths(1);
        LocalDateTime periodEnd = endDate != null ? endDate : LocalDateTime.now();

        FinancialReportResponse response = financialReportService.generateIncomeStatement(periodStart, periodEnd, username);
        return ResponseEntity.ok(ApiResponse.success("Income statement generated successfully", response));
    }
}
