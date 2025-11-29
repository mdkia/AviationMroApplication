package com.aviation.mro.modules.warehouse.domain.enums;

public enum WithdrawalStatus {
    PENDING,      // در انتظار تأیید
    APPROVED,     // تأیید شده
    REJECTED,     // رد شده
    COMPLETED,    // انجام شده (خروج انجام شد)
    CANCELLED     // لغو شده
}