package com.aviation.mro.modules.quality.domain.enums;

public enum DefectSeverity {
    CRITICAL,    // نقص بحرانی - توقف فوری
    MAJOR,       // نقص اصلی - نیاز به اقدام فوری
    MINOR,       // نقص جزئی - نیاز به اقدام برنامه‌ریزی شده
    COSMETIC     // نقص ظاهری - بدون تأثیر بر عملکرد
}
