package com.aviation.mro.modules.accounting.domain.enums;

public enum AccountCategory {
    // دارایی‌ها
    CURRENT_ASSETS,         // دارایی‌های جاری
    FIXED_ASSETS,           // دارایی‌های ثابت
    INTANGIBLE_ASSETS,      // دارایی‌های نامشهود

    // بدهی‌ها
    CURRENT_LIABILITIES,    // بدهی‌های جاری
    LONG_TERM_LIABILITIES,  // بدهی‌های بلندمدت

    // سرمایه
    OWNERS_EQUITY,          // سرمایه مالک

    // درآمدها
    OPERATING_REVENUE,      // درآمدهای عملیاتی
    NON_OPERATING_REVENUE,  // درآمدهای غیرعملیاتی

    // هزینه‌ها
    OPERATING_EXPENSE,      // هزینه‌های عملیاتی
    NON_OPERATING_EXPENSE,  // هزینه‌های غیرعملیاتی
    COST_OF_SALES           // بهای تمام شده
}
