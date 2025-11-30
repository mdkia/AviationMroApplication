package com.aviation.mro.modules.accounting.domain.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class TrialBalanceResponse {
    private LocalDateTime asOfDate;
    private Double totalDebit;
    private Double totalCredit;
    private Double difference;
    private Boolean isBalanced;
    private String periodCode;
    private LocalDateTime generatedAt;
    private List<TrialBalanceItem> accounts = new ArrayList<>();

    @Data
    public static class TrialBalanceItem {
        private Long accountId;
        private String accountCode;
        private String accountName;
        private Double debitBalance;
        private Double creditBalance;
    }
}
