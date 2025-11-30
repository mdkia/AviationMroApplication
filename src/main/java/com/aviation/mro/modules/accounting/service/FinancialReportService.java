package com.aviation.mro.modules.accounting.service;

import com.aviation.mro.modules.accounting.domain.dto.FinancialReportResponse;
import com.aviation.mro.modules.accounting.domain.model.Account;
import com.aviation.mro.modules.accounting.domain.enums.AccountType;
import com.aviation.mro.modules.accounting.domain.enums.FinancialStatementType;
import com.aviation.mro.modules.accounting.repository.AccountRepository;
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
public class FinancialReportService {

    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public FinancialReportResponse generateBalanceSheet(LocalDateTime asOfDate, String username) {
        List<Account> activeAccounts = accountRepository.findByIsActiveTrue();

        FinancialReportResponse response = new FinancialReportResponse();
        response.setReportType(FinancialStatementType.BALANCE_SHEET);
        response.setFromDate(asOfDate.minusMonths(1)); // Previous month for comparison
        response.setToDate(asOfDate);
        response.setGeneratedAt(LocalDateTime.now());
        response.setPeriodCode(generatePeriodCode(asOfDate));

        FinancialReportResponse.ReportSummary summary = new FinancialReportResponse.ReportSummary();

        // Assets
        List<Account> assetAccounts = activeAccounts.stream()
                .filter(account -> account.getAccountType() == AccountType.ASSET)
                .collect(Collectors.toList());

        List<FinancialReportResponse.FinancialReportItem> assetItems = assetAccounts.stream()
                .map(account -> createReportItem(account, account.getCurrentBalance()))
                .collect(Collectors.toList());

        double totalAssets = assetAccounts.stream()
                .mapToDouble(Account::getCurrentBalance)
                .sum();

        // Liabilities
        List<Account> liabilityAccounts = activeAccounts.stream()
                .filter(account -> account.getAccountType() == AccountType.LIABILITY)
                .collect(Collectors.toList());

        List<FinancialReportResponse.FinancialReportItem> liabilityItems = liabilityAccounts.stream()
                .map(account -> createReportItem(account, account.getCurrentBalance()))
                .collect(Collectors.toList());

        double totalLiabilities = liabilityAccounts.stream()
                .mapToDouble(Account::getCurrentBalance)
                .sum();

        // Equity
        List<Account> equityAccounts = activeAccounts.stream()
                .filter(account -> account.getAccountType() == AccountType.EQUITY)
                .collect(Collectors.toList());

        List<FinancialReportResponse.FinancialReportItem> equityItems = equityAccounts.stream()
                .map(account -> createReportItem(account, account.getCurrentBalance()))
                .collect(Collectors.toList());

        double totalEquity = equityAccounts.stream()
                .mapToDouble(Account::getCurrentBalance)
                .sum();

        // Add header items
        response.getItems().add(createHeaderItem("ASSETS", 1, true));
        response.getItems().addAll(assetItems);
        response.getItems().add(createHeaderItem("Total Assets", 1, false));
        response.getItems().add(createAmountItem(totalAssets, 1));

        response.getItems().add(createHeaderItem("LIABILITIES", 1, true));
        response.getItems().addAll(liabilityItems);
        response.getItems().add(createHeaderItem("Total Liabilities", 1, false));
        response.getItems().add(createAmountItem(totalLiabilities, 1));

        response.getItems().add(createHeaderItem("EQUITY", 1, true));
        response.getItems().addAll(equityItems);
        response.getItems().add(createHeaderItem("Total Equity", 1, false));
        response.getItems().add(createAmountItem(totalEquity, 1));

        summary.setTotalAssets(totalAssets);
        summary.setTotalLiabilities(totalLiabilities);
        summary.setTotalEquity(totalEquity);
        response.setSummary(summary);

        log.info("Balance sheet generated as of {} by user: {}", asOfDate, username);

        return response;
    }

    @Transactional(readOnly = true)
    public FinancialReportResponse generateIncomeStatement(LocalDateTime startDate, LocalDateTime endDate, String username) {
        List<Account> activeAccounts = accountRepository.findByIsActiveTrue();

        FinancialReportResponse response = new FinancialReportResponse();
        response.setReportType(FinancialStatementType.INCOME_STATEMENT);
        response.setFromDate(startDate);
        response.setToDate(endDate);
        response.setGeneratedAt(LocalDateTime.now());
        response.setPeriodCode(generatePeriodCode(startDate));

        FinancialReportResponse.ReportSummary summary = new FinancialReportResponse.ReportSummary();

        // Revenue accounts
        List<Account> revenueAccounts = activeAccounts.stream()
                .filter(account -> account.getAccountType() == AccountType.REVENUE)
                .collect(Collectors.toList());

        double totalRevenue = revenueAccounts.stream()
                .mapToDouble(Account::getCurrentBalance)
                .sum();

        // Expense accounts
        List<Account> expenseAccounts = activeAccounts.stream()
                .filter(account -> account.getAccountType() == AccountType.EXPENSE)
                .collect(Collectors.toList());

        double totalExpenses = expenseAccounts.stream()
                .mapToDouble(Account::getCurrentBalance)
                .sum();

        double netIncome = totalRevenue - totalExpenses;

        // Build report items
        response.getItems().add(createHeaderItem("REVENUE", 1, true));
        revenueAccounts.forEach(account ->
                response.getItems().add(createReportItem(account, account.getCurrentBalance())));
        response.getItems().add(createHeaderItem("Total Revenue", 1, false));
        response.getItems().add(createAmountItem(totalRevenue, 1));

        response.getItems().add(createHeaderItem("EXPENSES", 1, true));
        expenseAccounts.forEach(account ->
                response.getItems().add(createReportItem(account, account.getCurrentBalance())));
        response.getItems().add(createHeaderItem("Total Expenses", 1, false));
        response.getItems().add(createAmountItem(totalExpenses, 1));

        response.getItems().add(createHeaderItem("NET INCOME", 1, false));
        response.getItems().add(createAmountItem(netIncome, 1));

        summary.setTotalRevenue(totalRevenue);
        summary.setTotalExpenses(totalExpenses);
        summary.setNetIncome(netIncome);
        response.setSummary(summary);

        log.info("Income statement generated for period {} to {} by user: {}", startDate, endDate, username);

        return response;
    }

    private FinancialReportResponse.FinancialReportItem createReportItem(Account account, Double amount) {
        FinancialReportResponse.FinancialReportItem item = new FinancialReportResponse.FinancialReportItem();
        item.setAccountCode(account.getAccountCode());
        item.setAccountName(account.getAccountName());
        item.setAmount(amount);
        item.setLevel(account.getLevel());
        item.setIsHeader(false);
        return item;
    }

    private FinancialReportResponse.FinancialReportItem createHeaderItem(String title, Integer level, Boolean isSectionHeader) {
        FinancialReportResponse.FinancialReportItem item = new FinancialReportResponse.FinancialReportItem();
        item.setAccountName(title);
        item.setLevel(level);
        item.setIsHeader(isSectionHeader);
        return item;
    }

    private FinancialReportResponse.FinancialReportItem createAmountItem(Double amount, Integer level) {
        FinancialReportResponse.FinancialReportItem item = new FinancialReportResponse.FinancialReportItem();
        item.setAmount(amount);
        item.setLevel(level);
        item.setIsHeader(false);
        return item;
    }

    private String generatePeriodCode(LocalDateTime date) {
        return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
    }
}
