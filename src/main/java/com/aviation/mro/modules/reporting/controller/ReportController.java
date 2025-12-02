package com.aviation.mro.modules.reporting.controller;

import com.aviation.mro.modules.reporting.service.ReportService;
import com.aviation.mro.shared.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reporting", description = "گزارش‌های مدیریتی و عملیاتی سیستم MRO")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/dashboard-summary")
    @Operation(summary = "خلاصه داشبورد مدیریتی")
    public ResponseEntity<ApiResponse> getDashboardSummary() {
        return ResponseEntity.ok(ApiResponse.success("خلاصه داشبورد", reportService.getDashboardSummary()));
    }

    @GetMapping("/inventory-status")
    @Operation(summary = "گزارش وضعیت موجودی انبار")
    public ResponseEntity<ApiResponse> getInventoryStatusReport(
            @RequestParam(required = false) String partNumber,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success("گزارش موجودی انبار", reportService.getInventoryStatusReport(partNumber, status)));
    }

    @GetMapping("/quality/non-conformances")
    @Operation(summary = "گزارش عدم انطباق‌ها")
    public ResponseEntity<ApiResponse> getNonConformanceReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success("گزارش عدم انطباق‌ها", reportService.getNonConformanceReport(startDate, endDate, severity, status)));
    }

    @GetMapping("/repair/work-orders")
    @Operation(summary = "گزارش ورک اردرها")
    public ResponseEntity<ApiResponse> getWorkOrderReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success("گزارش ورک اردرها", reportService.getWorkOrderReport(startDate, endDate, status)));
    }

    @GetMapping("/accounting/financial-summary")
    @Operation(summary = "خلاصه مالی")
    public ResponseEntity<ApiResponse> getFinancialSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(ApiResponse.success("خلاصه مالی", reportService.getFinancialSummary(startDate, endDate)));
    }

    @GetMapping("/sales/performance")
    @Operation(summary = "عملکرد فروش")
    public ResponseEntity<ApiResponse> getSalesPerformance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(ApiResponse.success("عملکرد فروش", reportService.getSalesPerformance(startDate, endDate)));
    }
}