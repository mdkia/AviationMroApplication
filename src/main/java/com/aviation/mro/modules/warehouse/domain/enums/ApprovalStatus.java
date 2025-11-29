package com.aviation.mro.modules.warehouse.domain.enums;

// 🔐 Enum وضعیت‌های تأیید
public enum ApprovalStatus {
    NOT_REQUIRED,       // نیاز به تأیید ندارد
    PENDING_APPROVAL,   // در انتظار تأیید
    APPROVED,           // تأیید شده
    REJECTED,           // رد شده
    CANCELLED           // لغو شده
}
