package com.aviation.mro.modules.accounting.service;

import com.aviation.mro.modules.accounting.domain.dto.AccountRequest;
import com.aviation.mro.modules.accounting.domain.dto.AccountResponse;
import com.aviation.mro.modules.accounting.domain.model.Account;
import com.aviation.mro.modules.accounting.domain.enums.AccountType;
import com.aviation.mro.modules.accounting.repository.AccountRepository;
import com.aviation.mro.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public AccountResponse createAccount(AccountRequest request, String username) {
        // Validate account code uniqueness
        if (accountRepository.existsByAccountCode(request.getAccountCode())) {
            throw new IllegalArgumentException("Account code already exists: " + request.getAccountCode());
        }

        Account account = new Account();
        account.setAccountCode(request.getAccountCode());
        account.setAccountName(request.getAccountName());
        account.setDescription(request.getDescription());
        account.setAccountType(request.getAccountType());
        account.setCategory(request.getCategory());
        account.setIsParent(request.getIsParent());
        account.setOpeningBalance(request.getOpeningBalance());
        account.setCurrentBalance(request.getOpeningBalance());
        account.setDisplayOrder(request.getDisplayOrder());
        account.setLevel(request.getLevel());
        account.setCreatedBy(username);

        // Set parent account if provided
        if (request.getParentAccountId() != null) {
            Account parentAccount = accountRepository.findById(request.getParentAccountId())
                    .orElseThrow(() -> new NotFoundException("Parent account not found with id: " + request.getParentAccountId()));
            account.setParentAccount(parentAccount);
        }

        Account savedAccount = accountRepository.save(account);
        log.info("Account created: {} - {} by user: {}", request.getAccountCode(), request.getAccountName(), username);

        return mapToAccountResponse(savedAccount);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::mapToAccountResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Account not found with id: " + id));
        return mapToAccountResponse(account);
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountByCode(String accountCode) {
        Account account = accountRepository.findByAccountCode(accountCode)
                .orElseThrow(() -> new NotFoundException("Account not found with code: " + accountCode));
        return mapToAccountResponse(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByType(AccountType accountType) {
        return accountRepository.findByAccountType(accountType).stream()
                .map(this::mapToAccountResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getActiveAccounts() {
        return accountRepository.findByIsActiveTrue().stream()
                .map(this::mapToAccountResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getRootAccounts() {
        return accountRepository.findRootAccounts().stream()
                .map(this::mapToAccountResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AccountResponse updateAccountBalance(Long accountId, Double debitAmount, Double creditAmount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found with id: " + accountId));

        account.updateBalance(debitAmount, creditAmount);
        Account updatedAccount = accountRepository.save(account);

        log.debug("Account balance updated: {} - Debit: {}, Credit: {}",
                account.getAccountCode(), debitAmount, creditAmount);

        return mapToAccountResponse(updatedAccount);
    }

    // Mapping helper
    private AccountResponse mapToAccountResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setAccountCode(account.getAccountCode());
        response.setFullAccountCode(account.getFullAccountCode());
        response.setAccountName(account.getAccountName());
        response.setDescription(account.getDescription());
        response.setAccountType(account.getAccountType());
        response.setCategory(account.getCategory());
        response.setIsParent(account.getIsParent());
        response.setOpeningBalance(account.getOpeningBalance());
        response.setCurrentBalance(account.getCurrentBalance());
        response.setDebitTotal(account.getDebitTotal());
        response.setCreditTotal(account.getCreditTotal());
        response.setIsActive(account.getIsActive());
        response.setDisplayOrder(account.getDisplayOrder());
        response.setLevel(account.getLevel());
        response.setCreatedAt(account.getCreatedAt());
        response.setUpdatedAt(account.getUpdatedAt());

        if (account.getParentAccount() != null) {
            response.setParentAccountId(account.getParentAccount().getId());
            response.setParentAccountName(account.getParentAccount().getAccountName());
        }

        return response;
    }
}