package com.aviation.mro.modules.parts.repository;

import com.aviation.mro.modules.parts.domain.model.AircraftPart;
import com.aviation.mro.modules.parts.domain.enums.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AircraftPartRepository extends JpaRepository<AircraftPart, Long> {

    // ============ متدهای اصلی ============

    Optional<AircraftPart> findByPartNumber(String partNumber);

    Optional<AircraftPart> findBySerialNumber(String serialNumber);

    Optional<AircraftPart> findByPartNumberOrSerialNumber(String partNumber, String serialNumber);

    boolean existsByPartNumber(String partNumber);

    boolean existsBySerialNumber(String serialNumber);

    // ============ متدهای جستجو ============

    @Query("SELECT p FROM AircraftPart p WHERE " +
            "LOWER(p.partNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.partName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.serialNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.batchNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<AircraftPart> search(@Param("keyword") String keyword);

    @Query("SELECT p FROM AircraftPart p WHERE " +
            "LOWER(p.partNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.partName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.serialNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.batchNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<AircraftPart> search(@Param("keyword") String keyword, Pageable pageable);

    // ============ متدهای فیلتر بر اساس وضعیت ============

    List<AircraftPart> findByServiceabilityStatus(ServiceabilityStatus status);

    List<AircraftPart> findByLocationStatus(LocationStatus status);

    List<AircraftPart> findByCertificationStatus(CertificationStatus status);

    List<AircraftPart> findByServiceabilityStatusAndLocationStatus(
            ServiceabilityStatus serviceabilityStatus,
            LocationStatus locationStatus);

    // ============ متدهای فیلتر بر اساس AD ============

    List<AircraftPart> findByUnderActiveADTrue();

    // ============ متدهای فیلتر بر اساس تاریخ ============

    List<AircraftPart> findByManufactureDateBetween(LocalDate startDate, LocalDate endDate);

    List<AircraftPart> findByEntryIntoServiceDateBetween(LocalDate startDate, LocalDate endDate);

    List<AircraftPart> findByManufactureDateBefore(LocalDate date);

    List<AircraftPart> findByEntryIntoServiceDateBefore(LocalDate date);

    // ============ متدهای فیلتر بر اساس ساعت پرواز ============

    List<AircraftPart> findByTotalFlightHoursGreaterThan(Integer hours);

    List<AircraftPart> findByTotalFlightHoursLessThan(Integer hours);

    List<AircraftPart> findByTotalFlightHoursBetween(Integer minHours, Integer maxHours);

    // ============ متدهای فیلتر بر اساس سیکل پرواز ============

    List<AircraftPart> findByTotalFlightCyclesGreaterThan(Integer cycles);

    List<AircraftPart> findByTotalFlightCyclesLessThan(Integer cycles);

    List<AircraftPart> findByTotalFlightCyclesBetween(Integer minCycles, Integer maxCycles);

    // ============ متدهای ترکیبی ============

    @Query("SELECT p FROM AircraftPart p WHERE " +
            "p.serviceabilityStatus = :serviceabilityStatus AND " +
            "p.totalFlightHours > :minHours")
    List<AircraftPart> findServiceableWithMinFlightHours(
            @Param("serviceabilityStatus") ServiceabilityStatus serviceabilityStatus,
            @Param("minHours") Integer minHours);

    // ============ متدهای گزارش ============

    @Query("SELECT COUNT(p) FROM AircraftPart p")
    Long countAllParts();

    @Query("SELECT p.serviceabilityStatus, COUNT(p) FROM AircraftPart p GROUP BY p.serviceabilityStatus")
    List<Object[]> countByServiceabilityStatus();

    @Query("SELECT p.locationStatus, COUNT(p) FROM AircraftPart p GROUP BY p.locationStatus")
    List<Object[]> countByLocationStatus();

    @Query("SELECT p.certificationStatus, COUNT(p) FROM AircraftPart p GROUP BY p.certificationStatus")
    List<Object[]> countByCertificationStatus();

    @Query("SELECT p.underActiveAD, COUNT(p) FROM AircraftPart p GROUP BY p.underActiveAD")
    List<Object[]> countByUnderActiveAD();

    @Query("SELECT AVG(p.totalFlightHours) FROM AircraftPart p WHERE p.totalFlightHours > 0")
    Double averageFlightHours();

    @Query("SELECT AVG(p.totalFlightCycles) FROM AircraftPart p WHERE p.totalFlightCycles > 0")
    Double averageFlightCycles();

    // ============ متدهای برای maintenance ============

    @Query("SELECT p FROM AircraftPart p WHERE " +
            "p.totalFlightHours >= :maintenanceThresholdHours OR " +
            "p.totalFlightCycles >= :maintenanceThresholdCycles")
    List<AircraftPart> findPartsDueForMaintenance(
            @Param("maintenanceThresholdHours") Integer maintenanceThresholdHours,
            @Param("maintenanceThresholdCycles") Integer maintenanceThresholdCycles);

    // ============ متدهای برای قطعات با AD فعال ============

    @Query("SELECT p FROM AircraftPart p WHERE " +
            "p.underActiveAD = true AND " +
            "p.adDetails IS NOT NULL")
    List<AircraftPart> findPartsWithActiveAD();

    // ============ متدهای کمکی ============

    @Query("SELECT DISTINCT p.partNumber FROM AircraftPart p ORDER BY p.partNumber")
    List<String> findAllPartNumbers();

    @Query("SELECT DISTINCT p.batchNumber FROM AircraftPart p WHERE p.batchNumber IS NOT NULL ORDER BY p.batchNumber")
    List<String> findAllBatchNumbers();
}