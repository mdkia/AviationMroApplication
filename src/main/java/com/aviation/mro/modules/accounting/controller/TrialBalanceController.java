package com.aviation.mro.modules.accounting.controller;

import com.aviation.mro.modules.accounting.domain.dto.TrialBalanceResponse;
import com.aviation.mro.modules.accounting.service.TrialBalanceService;
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
@RequestMapping("/api/accounting/trial-balance")
@RequiredArgsConstructor
@Tag(name = "Trial Balance", description = "APIs for generating trial balance reports")
public class TrialBalanceController {

    private final TrialBalanceService trialBalanceService;

    @GetMapping
    @Operation(summary = "Generate trial balance as of specific date")
    public ResponseEntity<ApiResponse> generateTrialBalance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime asOfDate,
            Authentication authentication) {

        String username = SecurityUtils.getCurrentUsername(authentication);
        LocalDateTime targetDate = asOfDate != null ? asOfDate : LocalDateTime.now();

        TrialBalanceResponse response = trialBalanceService.generateTrialBalance(targetDate, username);
        return ResponseEntity.ok(ApiResponse.success("Trial balance generated successfully", response));
    }

    @GetMapping("/period")
    @Operation(summary = "Generate trial balance for specific period")
    public ResponseEntity<ApiResponse> generateTrialBalanceForPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Authentication authentication) {

        String username = SecurityUtils.getCurrentUsername(authentication);
        TrialBalanceResponse response = trialBalanceService.generateTrialBalanceForPeriod(startDate, endDate, username);
        return ResponseEntity.ok(ApiResponse.success("Trial balance generated successfully", response));
    }
}
