package com.aviation.mro.modules.parts.service;

import com.aviation.mro.modules.parts.domain.dto.*;
import com.aviation.mro.modules.parts.domain.model.AircraftPart;
import com.aviation.mro.modules.parts.domain.enums.*;
import com.aviation.mro.modules.parts.repository.AircraftPartRepository;
import com.aviation.mro.shared.common.ApiResponse;
import com.aviation.mro.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AircraftPartService {

    private final AircraftPartRepository aircraftPartRepository;

    // ============ متدهای مشاهده ============

    public List<AircraftPart> getAllParts() {
        return aircraftPartRepository.findAll();
    }

    public Page<AircraftPart> getAllParts(Pageable pageable) {
        return aircraftPartRepository.findAll(pageable);
    }

    public Page<AircraftPart> searchParts(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return aircraftPartRepository.findAll(pageable);
        }
        return aircraftPartRepository.search(keyword.trim(), pageable);
    }

    public List<AircraftPart> getPartsList() {
        return aircraftPartRepository.findAll();
    }

    public AircraftPart getPartById(Long id) {
        return aircraftPartRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.withResource("AircraftPart", id));
    }

    public AircraftPart getPartDetailById(Long id) {
//        AircraftPart part = getPartById(id);
//        return convertToDetailDto(part);

        return getPartById(id);
    }

    public AircraftPart getPartByPartNumber(String partNumber) {
        return aircraftPartRepository.findByPartNumber(partNumber)
                .orElseThrow(() ->
                        ResourceNotFoundException.withResource("AircraftPart", "partNumber " + partNumber));
    }

    public AircraftPart getPartBySerialNumber(String serialNumber) {
        return aircraftPartRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() ->
                        ResourceNotFoundException.withResource("AircraftPart", "serialNumber " + serialNumber));
    }

    // ============ متدهای فیلتر ============

    public List<AircraftPart> getPartsByServiceabilityStatus(ServiceabilityStatus status) {
        return aircraftPartRepository.findByServiceabilityStatus(status);
    }

    public List<AircraftPart> getPartsByLocationStatus(LocationStatus status) {
        return aircraftPartRepository.findByLocationStatus(status);
    }

    public List<AircraftPart> getPartsByCertificationStatus(CertificationStatus status) {
        return aircraftPartRepository.findByCertificationStatus(status);
    }

    public List<AircraftPart> getPartsWithActiveAD() {
        return aircraftPartRepository.findByUnderActiveADTrue();
    }

    public List<AircraftPart> getPartsByManufactureDateRange(LocalDate startDate, LocalDate endDate) {
        return aircraftPartRepository.findByManufactureDateBetween(startDate, endDate);
    }

    public List<AircraftPart> getPartsByFlightHoursRange(Integer minHours, Integer maxHours) {
        return aircraftPartRepository.findByTotalFlightHoursBetween(minHours, maxHours);
    }

    public List<AircraftPart> getPartsByFlightCyclesRange(Integer minCycles, Integer maxCycles) {
        return aircraftPartRepository.findByTotalFlightCyclesBetween(minCycles, maxCycles);
    }

    // ============ متدهای ایجاد ============

    public AircraftPart createPart(AircraftPart part) {
        // بررسی تکراری نبودن شماره قطعه
        if (aircraftPartRepository.existsByPartNumber(part.getPartNumber())) {
            throw new IllegalArgumentException("Part number already exists: " + part.getPartNumber());
        }

        // بررسی تکراری نبودن شماره سریال
        if (aircraftPartRepository.existsBySerialNumber(part.getSerialNumber())) {
            throw new IllegalArgumentException("Serial number already exists: " + part.getSerialNumber());
        }

        // تنظیم وضعیت‌های پیش‌فرض (اگر تنظیم نشده باشند)
        if (part.getServiceabilityStatus() == null) {
            part.setServiceabilityStatus(ServiceabilityStatus.SERVICEABLE);
        }
        if (part.getLocationStatus() == null) {
            part.setLocationStatus(LocationStatus.IN_STOCK);
        }
        if (part.getCertificationStatus() == null) {
            part.setCertificationStatus(CertificationStatus.NEEDS_CERTIFICATION);
        }
        if (part.getUnderActiveAD() == null) {
            part.setUnderActiveAD(false);
        }

        return aircraftPartRepository.save(part);
    }

    public AircraftPart createPart(CreatePartRequest request) {
        AircraftPart part = new AircraftPart();

        // mapping از DTO به Entity
        part.setPartNumber(request.getPartNumber());
        part.setPartName(request.getPartName());
        part.setDescription(request.getDescription());
        part.setSerialNumber(request.getSerialNumber());
        part.setBatchNumber(request.getBatchNumber());

        // enum fields
        if (request.getServiceabilityStatus() != null) {
            part.setServiceabilityStatus(ServiceabilityStatus.valueOf(request.getServiceabilityStatus()));
        }
        if (request.getLocationStatus() != null) {
            part.setLocationStatus(LocationStatus.valueOf(request.getLocationStatus()));
        }
        if (request.getCertificationStatus() != null) {
            part.setCertificationStatus(CertificationStatus.valueOf(request.getCertificationStatus()));
        }

        part.setUnderActiveAD(request.getUnderActiveAD());
        part.setAdDetails(request.getAdDetails());
        part.setManufactureDate(request.getManufactureDate());
        part.setEntryIntoServiceDate(request.getEntryIntoServiceDate());
        part.setTotalFlightHours(request.getTotalFlightHours() != null ? request.getTotalFlightHours() : 0);
        part.setTotalFlightCycles(request.getTotalFlightCycles() != null ? request.getTotalFlightCycles() : 0);

        return createPart(part);
    }

    public void createPartsBatch(List<AircraftPart> parts) {
        for (AircraftPart part : parts) {
            createPart(part);
        }
    }

    // ============ متدهای ویرایش ============

    public AircraftPart updatePart(Long id, AircraftPart updatedPart) {
        AircraftPart existingPart = getPartById(id);

        // بررسی تغییر شماره قطعه (اگر تغییر کرده)
        if (!existingPart.getPartNumber().equals(updatedPart.getPartNumber()) &&
                aircraftPartRepository.existsByPartNumber(updatedPart.getPartNumber())) {
            throw new IllegalArgumentException("Part number already exists: " + updatedPart.getPartNumber());
        }

        // بررسی تغییر شماره سریال (اگر تغییر کرده)
        if (!existingPart.getSerialNumber().equals(updatedPart.getSerialNumber()) &&
                aircraftPartRepository.existsBySerialNumber(updatedPart.getSerialNumber())) {
            throw new IllegalArgumentException("Serial number already exists: " + updatedPart.getSerialNumber());
        }

        // به‌روزرسانی فیلدها
        existingPart.setPartNumber(updatedPart.getPartNumber());
        existingPart.setPartName(updatedPart.getPartName());
        existingPart.setDescription(updatedPart.getDescription());
        existingPart.setSerialNumber(updatedPart.getSerialNumber());
        existingPart.setBatchNumber(updatedPart.getBatchNumber());
        existingPart.setServiceabilityStatus(updatedPart.getServiceabilityStatus());
        existingPart.setLocationStatus(updatedPart.getLocationStatus());
        existingPart.setCertificationStatus(updatedPart.getCertificationStatus());
        existingPart.setUnderActiveAD(updatedPart.getUnderActiveAD());
        existingPart.setAdDetails(updatedPart.getAdDetails());
        existingPart.setManufactureDate(updatedPart.getManufactureDate());
        existingPart.setEntryIntoServiceDate(updatedPart.getEntryIntoServiceDate());
        existingPart.setTotalFlightHours(updatedPart.getTotalFlightHours());
        existingPart.setTotalFlightCycles(updatedPart.getTotalFlightCycles());

        return aircraftPartRepository.save(existingPart);
    }

    public AircraftPart updatePart(Long id, UpdatePartRequest request) {
        AircraftPart existingPart = getPartById(id);

        // به‌روزرسانی فیلدها (فقط فیلدهایی که در request هستند)
        if (request.getPartNumber() != null) {
            if (!existingPart.getPartNumber().equals(request.getPartNumber()) &&
                    aircraftPartRepository.existsByPartNumber(request.getPartNumber())) {
                throw new IllegalArgumentException("Part number already exists: " + request.getPartNumber());
            }
            existingPart.setPartNumber(request.getPartNumber());
        }

        if (request.getPartName() != null) existingPart.setPartName(request.getPartName());
        if (request.getDescription() != null) existingPart.setDescription(request.getDescription());

        if (request.getSerialNumber() != null) {
            if (!existingPart.getSerialNumber().equals(request.getSerialNumber()) &&
                    aircraftPartRepository.existsBySerialNumber(request.getSerialNumber())) {
                throw new IllegalArgumentException("Serial number already exists: " + request.getSerialNumber());
            }
            existingPart.setSerialNumber(request.getSerialNumber());
        }

        if (request.getBatchNumber() != null) existingPart.setBatchNumber(request.getBatchNumber());
        if (request.getServiceabilityStatus() != null) {
            existingPart.setServiceabilityStatus(
                    ServiceabilityStatus.valueOf(request.getServiceabilityStatus()));
        }
        if (request.getLocationStatus() != null) {
            existingPart.setLocationStatus(LocationStatus.valueOf(request.getLocationStatus()));
        }
        if (request.getCertificationStatus() != null) {
            existingPart.setCertificationStatus(
                    CertificationStatus.valueOf(request.getCertificationStatus()));
        }
        if (request.getUnderActiveAD() != null) existingPart.setUnderActiveAD(request.getUnderActiveAD());
        if (request.getAdDetails() != null) existingPart.setAdDetails(request.getAdDetails());
        if (request.getManufactureDate() != null) existingPart.setManufactureDate(request.getManufactureDate());
        if (request.getEntryIntoServiceDate() != null) existingPart.setEntryIntoServiceDate(request.getEntryIntoServiceDate());
        if (request.getTotalFlightHours() != null) existingPart.setTotalFlightHours(request.getTotalFlightHours());
        if (request.getTotalFlightCycles() != null) existingPart.setTotalFlightCycles(request.getTotalFlightCycles());

        return aircraftPartRepository.save(existingPart);
    }

    public AircraftPart updateServiceabilityStatus(Long id, ServiceabilityStatus status) {
        AircraftPart part = getPartById(id);
        part.setServiceabilityStatus(status);
        return aircraftPartRepository.save(part);
    }

    public AircraftPart updateLocationStatus(Long id, LocationStatus status) {
        AircraftPart part = getPartById(id);
        part.setLocationStatus(status);
        return aircraftPartRepository.save(part);
    }

    public AircraftPart updateCertificationStatus(Long id, CertificationStatus status) {
        AircraftPart part = getPartById(id);
        part.setCertificationStatus(status);
        return aircraftPartRepository.save(part);
    }

    public AircraftPart updateFlightHours(Long id, Integer additionalHours) {
        AircraftPart part = getPartById(id);
        part.setTotalFlightHours(part.getTotalFlightHours() + additionalHours);
        return aircraftPartRepository.save(part);
    }

    public AircraftPart updateFlightCycles(Long id, Integer additionalCycles) {
        AircraftPart part = getPartById(id);
        part.setTotalFlightCycles(part.getTotalFlightCycles() + additionalCycles);
        return aircraftPartRepository.save(part);
    }

    // ============ متدهای تأیید و رد ============

    public AircraftPart certifyPart(Long id) {
        AircraftPart part = getPartById(id);

        if (part.getCertificationStatus() != CertificationStatus.NEEDS_CERTIFICATION) {
            throw new IllegalStateException("Part is not in NEEDS_CERTIFICATION status");
        }

        part.setCertificationStatus(CertificationStatus.CERTIFIED);
        return aircraftPartRepository.save(part);
    }

    public AircraftPart rejectCertification(Long id, String reason) {
        AircraftPart part = getPartById(id);

        if (part.getCertificationStatus() != CertificationStatus.NEEDS_CERTIFICATION) {
            throw new IllegalStateException("Part is not in NEEDS_CERTIFICATION status");
        }

        part.setCertificationStatus(CertificationStatus.REJECTED);
        part.setAdDetails((part.getAdDetails() != null ? part.getAdDetails() + "\n" : "") +
                "Certification rejection reason: " + reason);
        return aircraftPartRepository.save(part);
    }

    // ============ متدهای حذف ============

    public void deletePart(Long id) {
        if (!aircraftPartRepository.existsById(id)) {
            throw ResourceNotFoundException.withResource("AircraftPart", id);
        }
        aircraftPartRepository.deleteById(id);
    }

    // ============ متدهای گزارش ============

    public ApiResponse getDashboardStats() {
        Long totalParts = aircraftPartRepository.countAllParts();
        Double avgFlightHours = aircraftPartRepository.averageFlightHours();
        Double avgFlightCycles = aircraftPartRepository.averageFlightCycles();

        List<Object[]> serviceabilityStats = aircraftPartRepository.countByServiceabilityStatus();
        List<Object[]> locationStats = aircraftPartRepository.countByLocationStatus();
        List<Object[]> certificationStats = aircraftPartRepository.countByCertificationStatus();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalParts", totalParts != null ? totalParts : 0);
        stats.put("averageFlightHours", avgFlightHours != null ? avgFlightHours : 0.0);
        stats.put("averageFlightCycles", avgFlightCycles != null ? avgFlightCycles : 0.0);

        Map<String, Long> serviceabilityMap = new HashMap<>();
        for (Object[] row : serviceabilityStats) {
            serviceabilityMap.put(((ServiceabilityStatus) row[0]).name(), (Long) row[1]);
        }
        stats.put("serviceabilityDistribution", serviceabilityMap);

        Map<String, Long> locationMap = new HashMap<>();
        for (Object[] row : locationStats) {
            locationMap.put(((LocationStatus) row[0]).name(), (Long) row[1]);
        }
        stats.put("locationDistribution", locationMap);

        Map<String, Long> certificationMap = new HashMap<>();
        for (Object[] row : certificationStats) {
            certificationMap.put(((CertificationStatus) row[0]).name(), (Long) row[1]);
        }
        stats.put("certificationDistribution", certificationMap);

        return ApiResponse.success("Dashboard statistics", stats);
    }

    public List<AircraftPart> getPartsDueForMaintenance(Integer thresholdHours, Integer thresholdCycles) {
        return aircraftPartRepository.findPartsDueForMaintenance(thresholdHours, thresholdCycles);
    }

    // ============ متدهای DTO conversion ============

    private AircraftPartDetailDto convertToDetailDto(AircraftPart part) {
        AircraftPartDetailDto dto = new AircraftPartDetailDto();

        dto.setId(part.getId());
        dto.setPartNumber(part.getPartNumber());
        dto.setPartName(part.getPartName());
        dto.setDescription(part.getDescription());
        dto.setSerialNumber(part.getSerialNumber());
        dto.setBatchNumber(part.getBatchNumber());
        dto.setServiceabilityStatus(part.getServiceabilityStatus().name());
        dto.setLocationStatus(part.getLocationStatus().name());
        dto.setCertificationStatus(part.getCertificationStatus().name());
        dto.setUnderActiveAD(part.getUnderActiveAD());
        dto.setAdDetails(part.getAdDetails());
        dto.setManufactureDate(part.getManufactureDate());
        dto.setEntryIntoServiceDate(part.getEntryIntoServiceDate());
        dto.setTotalFlightHours(part.getTotalFlightHours());
        dto.setTotalFlightCycles(part.getTotalFlightCycles());
        dto.setCreatedAt(part.getCreatedAt());
        dto.setUpdatedAt(part.getUpdatedAt());

        return dto;
    }

    public ApiResponse getPartsCount() {
        Long count = aircraftPartRepository.countAllParts();
        return ApiResponse.success("Parts count retrieved", count != null ? count : 0);
    }
}