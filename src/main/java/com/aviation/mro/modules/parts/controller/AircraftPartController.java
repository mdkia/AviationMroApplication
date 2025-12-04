package com.aviation.mro.modules.parts.controller;

import com.aviation.mro.modules.parts.domain.dto.CreatePartRequest;
import com.aviation.mro.modules.parts.domain.dto.*;
import com.aviation.mro.modules.parts.domain.model.AircraftPart;
import com.aviation.mro.modules.parts.domain.enums.*;
import com.aviation.mro.modules.parts.service.AircraftPartService;
import com.aviation.mro.shared.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/parts")
@Tag(name = "Aircraft Parts Management", description = "مدیریت قطعات هواپیما")
public class AircraftPartController {

    private final AircraftPartService partService;

    public AircraftPartController(AircraftPartService partService) {
        this.partService = partService;
    }

    // ============ مشاهده و جستجو ============

    @GetMapping
    @Operation(summary = "دریافت همه قطعات (صفحه‌بندی)")
    @PreAuthorize("hasAuthority('VIEW_PARTS')")
    public ResponseEntity<Page<AircraftPart>> getAllParts(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(partService.searchParts(search.trim(), pageable));
        }
        return ResponseEntity.ok(partService.getAllParts(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "دریافت قطعه بر اساس ID")
    @PreAuthorize("hasAuthority('VIEW_PARTS')")
    public ResponseEntity<AircraftPart> getPartById(@PathVariable Long id) {
        return ResponseEntity.ok(partService.getPartDetailById(id));
    }

    @GetMapping("/part-number/{partNumber}")
    @Operation(summary = "دریافت قطعه بر اساس شماره قطعه")
    @PreAuthorize("hasAuthority('VIEW_PARTS')")
    public ResponseEntity<AircraftPart> getPartByPartNumber(@PathVariable String partNumber) {
        return ResponseEntity.ok(partService.getPartByPartNumber(partNumber));
    }

    @GetMapping("/serial/{serialNumber}")
    @Operation(summary = "دریافت قطعه بر اساس شماره سریال")
    @PreAuthorize("hasAuthority('VIEW_PARTS')")
    public ResponseEntity<AircraftPart> getPartBySerialNumber(@PathVariable String serialNumber) {
        return ResponseEntity.ok(partService.getPartBySerialNumber(serialNumber));
    }

    // ============ فیلترها ============

    @GetMapping("/status/serviceability/{status}")
    @Operation(summary = "دریافت قطعات بر اساس وضعیت سرویس‌پذیری")
    @PreAuthorize("hasAuthority('VIEW_PARTS')")
    public ResponseEntity<List<AircraftPart>> getPartsByServiceabilityStatus(
            @PathVariable ServiceabilityStatus status) {
        return ResponseEntity.ok(partService.getPartsByServiceabilityStatus(status));
    }

    @GetMapping("/status/location/{status}")
    @Operation(summary = "دریافت قطعات بر اساس وضعیت مکان")
    @PreAuthorize("hasAuthority('VIEW_PARTS')")
    public ResponseEntity<List<AircraftPart>> getPartsByLocationStatus(
            @PathVariable LocationStatus status) {
        return ResponseEntity.ok(partService.getPartsByLocationStatus(status));
    }

    @GetMapping("/with-active-ad")
    @Operation(summary = "دریافت قطعات با AD فعال")
    @PreAuthorize("hasAuthority('VIEW_PARTS')")
    public ResponseEntity<List<AircraftPart>> getPartsWithActiveAD() {
        return ResponseEntity.ok(partService.getPartsWithActiveAD());
    }

    // ============ ایجاد ============

    @PostMapping
    @Operation(summary = "ایجاد قطعه جدید")
    @PreAuthorize("hasAuthority('CREATE_PARTS')")
    public ResponseEntity<AircraftPart> createPart(@Valid @RequestBody CreatePartRequest request) {
        return ResponseEntity.ok(partService.createPart(request));
    }

    // ============ ویرایش ============

    @PutMapping("/{id}")
    @Operation(summary = "ویرایش کامل قطعه")
    @PreAuthorize("hasAuthority('EDIT_PARTS')")
    public ResponseEntity<AircraftPart> updatePart(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePartRequest request) {
        return ResponseEntity.ok(partService.updatePart(id, request));
    }

    @PatchMapping("/{id}/serviceability-status")
    @Operation(summary = "تغییر وضعیت سرویس‌پذیری قطعه")
    @PreAuthorize("hasAuthority('EDIT_PARTS')")
    public ResponseEntity<AircraftPart> updateServiceabilityStatus(
            @PathVariable Long id,
            @RequestParam ServiceabilityStatus status) {
        return ResponseEntity.ok(partService.updateServiceabilityStatus(id, status));
    }

    @PatchMapping("/{id}/location-status")
    @Operation(summary = "تغییر وضعیت مکان قطعه")
    @PreAuthorize("hasAuthority('EDIT_PARTS')")
    public ResponseEntity<AircraftPart> updateLocationStatus(
            @PathVariable Long id,
            @RequestParam LocationStatus status) {
        return ResponseEntity.ok(partService.updateLocationStatus(id, status));
    }

    @PatchMapping("/{id}/flight-hours")
    @Operation(summary = "افزایش ساعت پرواز قطعه")
    @PreAuthorize("hasAuthority('EDIT_PARTS')")
    public ResponseEntity<AircraftPart> updateFlightHours(
            @PathVariable Long id,
            @RequestParam Integer additionalHours) {
        return ResponseEntity.ok(partService.updateFlightHours(id, additionalHours));
    }

    // ============ تأیید و صدور گواهی ============

    @PostMapping("/{id}/certify")
    @Operation(summary = "صدور گواهی برای قطعه")
    @PreAuthorize("hasAuthority('APPROVE_PARTS')")
    public ResponseEntity<AircraftPart> certifyPart(@PathVariable Long id) {
        return ResponseEntity.ok(partService.certifyPart(id));
    }

    @PostMapping("/{id}/reject-certification")
    @Operation(summary = "رد گواهی قطعه")
    @PreAuthorize("hasAuthority('REJECT_PARTS')")
    public ResponseEntity<AircraftPart> rejectCertification(
            @PathVariable Long id,
            @RequestParam String reason) {
        return ResponseEntity.ok(partService.rejectCertification(id, reason));
    }

    // ============ حذف ============

    @DeleteMapping("/{id}")
    @Operation(summary = "حذف قطعه")
    @PreAuthorize("hasAuthority('DELETE_PARTS')")
    public ResponseEntity<ApiResponse> deletePart(@PathVariable Long id) {
        partService.deletePart(id);
        return ResponseEntity.ok(ApiResponse.success("قطعه با موفقیت حذف شد"));
    }

    // ============ گزارشات ============

    @GetMapping("/dashboard/stats")
    @Operation(summary = "آمار داشبورد قطعات")
    @PreAuthorize("hasAnyAuthority('VIEW_PARTS', 'VIEW_REPORTS')")
    public ResponseEntity<ApiResponse> getDashboardStats() {
        return ResponseEntity.ok(partService.getDashboardStats());
    }

    @GetMapping("/maintenance/due")
    @Operation(summary = "قطعات نیازمند تعمیر")
    @PreAuthorize("hasAnyAuthority('VIEW_PARTS', 'MANAGE_REPAIR')")
    public ResponseEntity<List<AircraftPart>> getPartsDueForMaintenance(
            @RequestParam(defaultValue = "1000") Integer thresholdHours,
            @RequestParam(defaultValue = "100") Integer thresholdCycles) {
        return ResponseEntity.ok(
                partService.getPartsDueForMaintenance(thresholdHours, thresholdCycles));
    }

    @GetMapping("/count")
    @Operation(summary = "تعداد قطعات")
    @PreAuthorize("hasAuthority('VIEW_PARTS')")
    public ResponseEntity<ApiResponse> getPartsCount() {
        return ResponseEntity.ok(partService.getPartsCount());
    }
}