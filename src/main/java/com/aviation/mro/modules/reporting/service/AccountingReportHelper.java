// AccountingReportHelper.java
package com.aviation.mro.modules.reporting.service;

import com.aviation.mro.modules.accounting.domain.model.JournalEntry;
import com.aviation.mro.modules.accounting.domain.model.JournalItem;
import com.aviation.mro.modules.accounting.repository.JournalEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AccountingReportHelper {

    private final JournalEntryRepository journalEntryRepository;

    /**
     * درآمد این ماه (از اسناد ثبت‌شده با حساب‌های نوع REVENUE)
     */
    public BigDecimal getRevenueThisMonth() {
        LocalDateTime startOfMonth = LocalDateTime.now()
                .withDayOfMonth(1)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);

        return journalEntryRepository.findAll().stream()
                .filter(je -> je.getIsPosted() != null && je.getIsPosted())
                .filter(je -> je.getEntryDate() != null && !je.getEntryDate().isBefore(startOfMonth))
                .flatMap(je -> je.getItems().stream())
                .filter(item -> item.getAccount() != null)
                .filter(item -> "REVENUE".equalsIgnoreCase(item.getAccount().getAccountType().name()))
                .filter(item -> item.getCreditAmount() != null && item.getCreditAmount() > 0)
                .map(item -> BigDecimal.valueOf(item.getCreditAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * خلاصه مالی در بازه زمانی مشخص
     */
    public Map<String, Object> getFinancialSummary(LocalDateTime start, LocalDateTime end) {
        if (start == null) {
            start = LocalDateTime.now().minusMonths(1).withDayOfMonth(1)
                    .withHour(0).withMinute(0).withSecond(0);
        }
        if (end == null) {
            end = LocalDateTime.now();
        }

        final LocalDateTime finalStart = start;
        final LocalDateTime finalEnd = end;

        var postedEntries = journalEntryRepository.findAll().stream()
                .filter(je -> je.getIsPosted() != null && je.getIsPosted())
                .filter(je -> je.getEntryDate() != null)
                .filter(je -> !je.getEntryDate().isBefore(finalStart) && je.getEntryDate().isBefore(finalEnd))
                .toList();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (JournalEntry je : postedEntries) {
            for (JournalItem item : je.getItems()) {
                if (item.getAccount() == null) continue;

                String type = item.getAccount().getAccountType().name();
                Double credit = item.getCreditAmount() != null ? item.getCreditAmount() : 0.0;
                Double debit = item.getDebitAmount() != null ? item.getDebitAmount() : 0.0;

                if ("REVENUE".equalsIgnoreCase(type)) {
                    totalRevenue = totalRevenue.add(BigDecimal.valueOf(credit));
                } else if ("EXPENSE".equalsIgnoreCase(type) || "COST_OF_GOODS_SOLD".equalsIgnoreCase(type)) {
                    totalExpense = totalExpense.add(BigDecimal.valueOf(debit));
                }
            }
        }

        BigDecimal netProfit = totalRevenue.subtract(totalExpense);
        BigDecimal profitMargin = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                ? netProfit.divide(totalRevenue, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        return Map.of(
                "periodStart", finalStart,
                "periodEnd", finalEnd,
                "totalRevenue", totalRevenue,
                "totalExpense", totalExpense,
                "netProfit", netProfit,
                "profitMarginPercent", profitMargin,
                "totalPostedEntries", postedEntries.size(),
                "generatedAt", LocalDateTime.now()
        );
    }
}