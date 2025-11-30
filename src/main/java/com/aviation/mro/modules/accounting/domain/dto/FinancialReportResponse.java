package com.aviation.mro.modules.accounting.domain.dto;

import com.aviation.mro.modules.accounting.domain.enums.FinancialStatementType;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class FinancialReportResponse {
    private FinancialStatementType reportType;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String periodCode;
    private LocalDateTime generatedAt;
    private List<FinancialReportItem> items = new ArrayList<>();
    private ReportSummary summary;

    @Data
    public static class FinancialReportItem {
        private String accountCode;
        private String accountName;
        private Double amount;
        private Integer level;
        private Boolean isHeader;
    }

    @Data
    public static class ReportSummary {
        private Double totalAssets;
        private Double totalLiabilities;
        private Double totalEquity;
        private Double totalRevenue;
        private Double totalExpenses;
        private Double netIncome;
    }
}
