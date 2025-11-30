package com.aviation.mro.modules.accounting.controller;

import com.aviation.mro.modules.accounting.domain.dto.AccountRequest;
import com.aviation.mro.modules.accounting.domain.dto.AccountResponse;
import com.aviation.mro.modules.accounting.domain.enums.AccountType;
import com.aviation.mro.modules.accounting.service.AccountService;
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
@RequestMapping("/api/accounting/accounts")
@RequiredArgsConstructor
@Tag(name = "Account Management", description = "APIs for managing chart of accounts")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Create a new account")
    public ResponseEntity<ApiResponse> createAccount(
            @Valid @RequestBody AccountRequest request,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        AccountResponse response = accountService.createAccount(request, username);
        return ResponseEntity.ok(ApiResponse.success("Account created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all accounts")
    public ResponseEntity<ApiResponse> getAllAccounts() {
        List<AccountResponse> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(ApiResponse.success("Accounts retrieved successfully", accounts));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account by ID")
    public ResponseEntity<ApiResponse> getAccountById(@PathVariable Long id) {
        AccountResponse account = accountService.getAccountById(id);
        return ResponseEntity.ok(ApiResponse.success("Account retrieved successfully", account));
    }

    @GetMapping("/code/{accountCode}")
    @Operation(summary = "Get account by code")
    public ResponseEntity<ApiResponse> getAccountByCode(@PathVariable String accountCode) {
        AccountResponse account = accountService.getAccountByCode(accountCode);
        return ResponseEntity.ok(ApiResponse.success("Account retrieved successfully", account));
    }

    @GetMapping("/type/{accountType}")
    @Operation(summary = "Get accounts by type")
    public ResponseEntity<ApiResponse> getAccountsByType(@PathVariable AccountType accountType) {
        List<AccountResponse> accounts = accountService.getAccountsByType(accountType);
        return ResponseEntity.ok(ApiResponse.success("Accounts retrieved successfully", accounts));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active accounts")
    public ResponseEntity<ApiResponse> getActiveAccounts() {
        List<AccountResponse> accounts = accountService.getActiveAccounts();
        return ResponseEntity.ok(ApiResponse.success("Active accounts retrieved successfully", accounts));
    }

    @GetMapping("/root")
    @Operation(summary = "Get root accounts")
    public ResponseEntity<ApiResponse> getRootAccounts() {
        List<AccountResponse> accounts = accountService.getRootAccounts();
        return ResponseEntity.ok(ApiResponse.success("Root accounts retrieved successfully", accounts));
    }
}