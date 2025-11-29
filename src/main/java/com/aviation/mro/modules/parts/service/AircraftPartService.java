package com.aviation.mro.modules.parts.service;


import com.aviation.mro.modules.parts.domain.model.AircraftPart;
import com.aviation.mro.modules.parts.domain.enums.ServiceabilityStatus;
import com.aviation.mro.modules.parts.repository.AircraftPartRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AircraftPartService {

    private final AircraftPartRepository partRepository;

    public AircraftPartService(AircraftPartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public List<AircraftPart> getAllParts() {
        return partRepository.findAll();
    }

    public AircraftPart getPartById(Long id) {
        return partRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Part not found with id: " + id));
    }

    public AircraftPart createPart(AircraftPart part) {
        if (partRepository.existsByPartNumber(part.getPartNumber())) {
            throw new RuntimeException("Part number already exists: " + part.getPartNumber());
        }
        if (partRepository.existsBySerialNumber(part.getSerialNumber())) {
            throw new RuntimeException("Serial number already exists: " + part.getSerialNumber());
        }
        return partRepository.save(part);
    }

    public AircraftPart updatePart(Long id, AircraftPart partDetails) {
        AircraftPart part = getPartById(id);

        part.setPartName(partDetails.getPartName());
        part.setDescription(partDetails.getDescription());
        part.setServiceabilityStatus(partDetails.getServiceabilityStatus());
        part.setLocationStatus(partDetails.getLocationStatus());
        part.setCertificationStatus(partDetails.getCertificationStatus());
        part.setUnderActiveAD(partDetails.getUnderActiveAD());
        part.setAdDetails(partDetails.getAdDetails());
        part.setTotalFlightHours(partDetails.getTotalFlightHours());
        part.setTotalFlightCycles(partDetails.getTotalFlightCycles());

        return partRepository.save(part);
    }

    public void deletePart(Long id) {
        AircraftPart part = getPartById(id);
        partRepository.delete(part);
    }

    public List<AircraftPart> getPartsByStatus(ServiceabilityStatus status) {
        return partRepository.findByServiceabilityStatus(status);
    }
}
