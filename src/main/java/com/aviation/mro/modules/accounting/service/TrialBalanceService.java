package com.aviation.mro.modules.accounting.service;

import com.aviation.mro.modules.accounting.domain.dto.TrialBalanceResponse;
import com.aviation.mro.modules.accounting.domain.model.Account;
import com.aviation.mro.modules.accounting.domain.model.TrialBalance;
import com.aviation.mro.modules.accounting.domain.enums.AccountType;
import com.aviation.mro.modules.accounting.repository.AccountRepository;
import com.aviation.mro.modules.accounting.repository.JournalItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrialBalanceService {

    private final AccountRepository accountRepository;
    private final JournalItemRepository journalItemRepository;

    @Transactional(readOnly = true)
    public TrialBalanceResponse generateTrialBalance(LocalDateTime asOfDate, String username) {
        List<Account> activeAccounts = accountRepository.findByIsActiveTrue();

        TrialBalanceResponse response = new TrialBalanceResponse();
        response.setAsOfDate(asOfDate);
        response.setGeneratedAt(LocalDateTime.now());
        response.setPeriodCode(generatePeriodCode(asOfDate));

        // استفاده از آرایه برای دور زدن محدودیت final
        double[] totals = new double[2]; // [0] = totalDebit, [1] = totalCredit

        // Calculate balances for each account
        List<TrialBalanceResponse.TrialBalanceItem> balanceItems = activeAccounts.stream()
                .map(account -> {
                    TrialBalanceResponse.TrialBalanceItem item = new TrialBalanceResponse.TrialBalanceItem();
                    item.setAccountId(account.getId());
                    item.setAccountCode(account.getFullAccountCode());
                    item.setAccountName(account.getAccountName());

                    // Determine debit/credit balance based on account type
                    if (account.getAccountType() == AccountType.ASSET || account.getAccountType() == AccountType.EXPENSE) {
                        item.setDebitBalance(account.getCurrentBalance());
                        item.setCreditBalance(0.0);
                        totals[0] += account.getCurrentBalance(); // totalDebit
                    } else {
                        item.setDebitBalance(0.0);
                        item.setCreditBalance(account.getCurrentBalance());
                        totals[1] += account.getCurrentBalance(); // totalCredit
                    }

                    return item;
                })
                .collect(Collectors.toList());

        response.setAccounts(balanceItems);
        response.setTotalDebit(totals[0]);
        response.setTotalCredit(totals[1]);
        response.setDifference(Math.abs(totals[0] - totals[1]));
        response.setIsBalanced(totals[0] == totals[1]);

        log.info("Trial balance generated as of {} by user: {}", asOfDate, username);

        return response;
    }

    @Transactional(readOnly = true)
    public TrialBalanceResponse generateTrialBalanceForPeriod(LocalDateTime startDate, LocalDateTime endDate, String username) {
        List<Account> activeAccounts = accountRepository.findByIsActiveTrue();

        TrialBalanceResponse response = new TrialBalanceResponse();
        response.setAsOfDate(endDate);
        response.setGeneratedAt(LocalDateTime.now());
        response.setPeriodCode(generatePeriodCode(startDate));

        // استفاده از AtomicReference برای mutable variables
        AtomicReference<Double> totalDebit = new AtomicReference<>(0.0);
        AtomicReference<Double> totalCredit = new AtomicReference<>(0.0);

        // Calculate balances for each account for the specific period
        List<TrialBalanceResponse.TrialBalanceItem> balanceItems = activeAccounts.stream()
                .map(account -> {
                    // Get period totals for the account
                    Double periodDebit = journalItemRepository.getAccountDebitTotal(account.getId(), startDate, endDate);
                    Double periodCredit = journalItemRepository.getAccountCreditTotal(account.getId(), startDate, endDate);

                    TrialBalanceResponse.TrialBalanceItem item = new TrialBalanceResponse.TrialBalanceItem();
                    item.setAccountId(account.getId());
                    item.setAccountCode(account.getFullAccountCode());
                    item.setAccountName(account.getAccountName());

                    double debitBalance = 0.0;
                    double creditBalance = 0.0;

                    // Determine debit/credit balance based on account type
                    if (account.getAccountType() == AccountType.ASSET || account.getAccountType() == AccountType.EXPENSE) {
                        double balance = (periodDebit != null ? periodDebit : 0.0) - (periodCredit != null ? periodCredit : 0.0);
                        debitBalance = Math.max(balance, 0);
                        creditBalance = Math.max(-balance, 0);
                        totalDebit.updateAndGet(v -> v + Math.max(balance, 0));
                        totalCredit.updateAndGet(v -> v + Math.max(-balance, 0));
                    } else {
                        double balance = (periodCredit != null ? periodCredit : 0.0) - (periodDebit != null ? periodDebit : 0.0);
                        debitBalance = Math.max(-balance, 0);
                        creditBalance = Math.max(balance, 0);
                        totalDebit.updateAndGet(v -> v + Math.max(-balance, 0));
                        totalCredit.updateAndGet(v -> v + Math.max(balance, 0));
                    }

                    item.setDebitBalance(debitBalance);
                    item.setCreditBalance(creditBalance);

                    return item;
                })
                .collect(Collectors.toList());

        response.setAccounts(balanceItems);
        response.setTotalDebit(totalDebit.get());
        response.setTotalCredit(totalCredit.get());
        response.setDifference(Math.abs(totalDebit.get() - totalCredit.get()));
        response.setIsBalanced(totalDebit.get().equals(totalCredit.get()));

        log.info("Trial balance generated for period {} to {} by user: {}", startDate, endDate, username);

        return response;
    }

    private String generatePeriodCode(LocalDateTime date) {
        return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
    }
}