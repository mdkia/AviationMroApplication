package com.aviation.mro.modules.reporting.service;

import com.aviation.mro.modules.quality.domain.enums.NCRStatus;
import com.aviation.mro.modules.quality.domain.enums.CorrectiveActionStatus;
import com.aviation.mro.modules.quality.domain.model.NonConformanceReport;
import com.aviation.mro.modules.quality.repository.NonConformanceReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class QualityReportHelper {

    private final NonConformanceReportRepository ncrRepository;

    // تعداد NCRهای باز (OPEN یا UNDER_INVESTIGATION)
    public long countOpenNonConformances() {
        return ncrRepository.findAll().stream()
                .filter(ncr -> ncr.getStatus() == NCRStatus.OPEN ||
                        ncr.getStatus() == NCRStatus.UNDER_INVESTIGATION)
                .count();
    }

    // تعداد NCRهای با تأخیر (تاریخ هدف گذشته و هنوز بسته نشده)
    public long countOverdueNonConformances() {
        return ncrRepository.findAll().stream()
                .filter(ncr -> ncr.getTargetCompletionDate() != null &&
                        ncr.getTargetCompletionDate().isBefore(LocalDateTime.now()) &&
                        ncr.getStatus() != NCRStatus.CLOSED)
                .count();
    }

    // گزارش کامل عدم انطباق‌ها با فیلترهای دلخواه
    public List<Map<String, Object>> getNonConformanceReport(
            LocalDateTime start,
            LocalDateTime end,
            String severity,        // اگر فیلد severity اضافه کردی
            String status) {

        List<NonConformanceReport> allNcrs = ncrRepository.findAll();

        return allNcrs.stream()
                .filter(ncr -> start == null ||
                        (ncr.getCreatedAt() != null && !ncr.getCreatedAt().isBefore(start)))
                .filter(ncr -> end == null ||
                        (ncr.getCreatedAt() != null && ncr.getCreatedAt().isBefore(end)))
                .filter(ncr -> status == null ||
                        (ncr.getStatus() != null && ncr.getStatus().name().equalsIgnoreCase(status.trim())))
                // اگر فیلد severity در مدل اضافه کردی، این خط رو فعال کن:
                // .filter(ncr -> severity == null || (ncr.getSeverity() != null && ncr.getSeverity().equalsIgnoreCase(severity.trim())))
                .map(ncr -> {
                    boolean isOverdue = ncr.getTargetCompletionDate() != null &&
                            ncr.getTargetCompletionDate().isBefore(LocalDateTime.now()) &&
                            ncr.getStatus() != NCRStatus.CLOSED;

                    return Map.<String, Object>ofEntries(
                            Map.entry("id", ncr.getId()),
                            Map.entry("ncrNumber", ncr.getNcrNumber() != null ? ncr.getNcrNumber() : ""),
                            Map.entry("problemDescription", safeString(ncr.getProblemDescription())),
                            Map.entry("rootCause", safeString(ncr.getRootCause())),
                            Map.entry("immediateAction", safeString(ncr.getImmediateAction())),
                            Map.entry("correctiveAction", safeString(ncr.getCorrectiveAction())),
                            Map.entry("preventiveAction", safeString(ncr.getPreventiveAction())),
                            Map.entry("status", ncr.getStatus() != null ? ncr.getStatus().name() : "نامشخص"),
                            Map.entry("statusInPersian", getPersianStatus(ncr.getStatus())),
                            Map.entry("correctiveActionStatus",
                                    ncr.getCorrectiveActionStatus() != null ? ncr.getCorrectiveActionStatus().name() : "نامشخص"),
                            Map.entry("correctiveActionStatusInPersian", getPersianCorrectiveAction(ncr.getCorrectiveActionStatus())),
                            Map.entry("raisedBy", safeString(ncr.getRaisedBy())),
                            Map.entry("assignedTo", safeString(ncr.getAssignedTo())),
                            Map.entry("verifiedBy", safeString(ncr.getVerifiedBy())),
                            Map.entry("targetCompletionDate", ncr.getTargetCompletionDate()),
                            Map.entry("actualCompletionDate", ncr.getActualCompletionDate()),
                            Map.entry("verificationDate", ncr.getVerificationDate()),
                            Map.entry("isEffective", ncr.getIsEffective()),
                            Map.entry("createdAt", ncr.getCreatedAt()),
                            Map.entry("isOverdue", isOverdue),
                            Map.entry("inspectionId", ncr.getInspection() != null ? ncr.getInspection().getId() : null)
                    );
                })
                .toList();
    }

    // تبدیل وضعیت NCR به فارسی
    private String getPersianStatus(NCRStatus status) {
        if (status == null) return "نامشخص";
        return switch (status) {
            case OPEN -> "باز";
            case UNDER_INVESTIGATION -> "در حال بررسی";
            case CORRECTIVE_ACTION_ISSUED -> "اقدام اصلاحی صادر شده";
            case VERIFICATION_PENDING -> "در انتظار تأیید";
            case CLOSED -> "بسته شده";
            case REOPENED -> "دوباره باز شده";
        };
    }

    // تبدیل وضعیت اقدام اصلاحی به فارسی
    private String getPersianCorrectiveAction(CorrectiveActionStatus status) {
        if (status == null) return "نامشخص";
        return switch (status) {
            case PENDING -> "در انتظار";
            case IN_PROGRESS -> "در حال انجام";
            case COMPLETED -> "تکمیل شده";
            case VERIFIED -> "تأیید شده";
            case REJECTED -> "رد شده";
            default -> status.name();
        };
    }

    // متد کمکی برای جلوگیری از null
    private String safeString(String value) {
        return value != null ? value : "";
    }
}