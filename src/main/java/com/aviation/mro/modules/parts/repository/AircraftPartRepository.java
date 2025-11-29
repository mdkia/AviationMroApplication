package com.aviation.mro.modules.parts.repository;


import com.aviation.mro.modules.parts.domain.model.AircraftPart;
import com.aviation.mro.modules.parts.domain.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AircraftPartRepository extends JpaRepository<AircraftPart, Long> {
    Optional<AircraftPart> findByPartNumber(String partNumber);
    Optional<AircraftPart> findBySerialNumber(String serialNumber);
    List<AircraftPart> findByServiceabilityStatus(ServiceabilityStatus status);
    List<AircraftPart> findByLocationStatus(LocationStatus status);
    boolean existsByPartNumber(String partNumber);
    boolean existsBySerialNumber(String serialNumber);
}