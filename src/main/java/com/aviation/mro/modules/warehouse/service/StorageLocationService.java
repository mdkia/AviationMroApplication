package com.aviation.mro.modules.warehouse.service;

import com.aviation.mro.modules.warehouse.domain.dto.StorageLocationRequest;
import com.aviation.mro.modules.warehouse.domain.dto.StorageLocationResponse;
import com.aviation.mro.modules.warehouse.domain.enums.StorageType;
import com.aviation.mro.modules.warehouse.domain.enums.TemperatureZone;
import com.aviation.mro.modules.warehouse.domain.model.StorageLocation;
import com.aviation.mro.modules.warehouse.domain.model.Warehouse;
import com.aviation.mro.modules.warehouse.repository.StorageLocationRepository;
import com.aviation.mro.modules.warehouse.repository.WarehouseRepository;
import com.aviation.mro.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class StorageLocationService {

    private final StorageLocationRepository storageLocationRepository;
    private final WarehouseRepository warehouseRepository;

    public List<StorageLocationResponse> getAllStorageLocations() {
        return storageLocationRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<StorageLocationResponse> getStorageLocationsByWarehouse(Long warehouseId) {
        return storageLocationRepository.findActiveByWarehouseId(warehouseId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public StorageLocationResponse getStorageLocationById(Long id) {
        StorageLocation location = storageLocationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Storage location not found with id: " + id));
        return convertToResponse(location);
    }

    public StorageLocationResponse createStorageLocation(StorageLocationRequest request) {
        if (storageLocationRepository.existsByLocationCode(request.getLocationCode())) {
            throw new RuntimeException("Storage location code already exists: " + request.getLocationCode());
        }

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new NotFoundException("Warehouse not found with id: " + request.getWarehouseId()));

        StorageLocation location = new StorageLocation();
        location.setLocationCode(request.getLocationCode());
        location.setName(request.getName());
        location.setDescription(request.getDescription());
        location.setMaxWeight(request.getMaxWeight());
        location.setMaxVolume(request.getMaxVolume());
        location.setStorageType(request.getStorageType() != null ?
                request.getStorageType() : StorageType.SHELF);
        location.setTemperatureZone(request.getTemperatureZone() != null ?
                request.getTemperatureZone() : TemperatureZone.AMBIENT);
        location.setWarehouse(warehouse);

        StorageLocation saved = storageLocationRepository.save(location);
        return convertToResponse(saved);
    }

    public StorageLocationResponse updateStorageLocation(Long id, StorageLocationRequest request) {
        StorageLocation location = storageLocationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Storage location not found with id: " + id));

        // بررسی یکتایی کد
        if (!location.getLocationCode().equals(request.getLocationCode()) &&
                storageLocationRepository.existsByLocationCode(request.getLocationCode())) {
            throw new RuntimeException("Storage location code already exists: " + request.getLocationCode());
        }

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new NotFoundException("Warehouse not found with id: " + request.getWarehouseId()));

        location.setLocationCode(request.getLocationCode());
        location.setName(request.getName());
        location.setDescription(request.getDescription());
        location.setMaxWeight(request.getMaxWeight());
        location.setMaxVolume(request.getMaxVolume());
        location.setStorageType(request.getStorageType());
        location.setTemperatureZone(request.getTemperatureZone());
        location.setWarehouse(warehouse);

        StorageLocation updated = storageLocationRepository.save(location);
        return convertToResponse(updated);
    }

    public void deleteStorageLocation(Long id) {
        StorageLocation location = storageLocationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Storage location not found with id: " + id));

        location.setActive(false);
        storageLocationRepository.save(location);
    }

    public List<StorageLocationResponse> searchStorageLocations(String keyword) {
        return storageLocationRepository.searchByLocationCodeOrName(keyword).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private StorageLocationResponse convertToResponse(StorageLocation location) {
        StorageLocationResponse response = new StorageLocationResponse();
        response.setId(location.getId());
        response.setLocationCode(location.getLocationCode());
        response.setName(location.getName());
        response.setDescription(location.getDescription());
        response.setMaxWeight(location.getMaxWeight());
        response.setMaxVolume(location.getMaxVolume());
        response.setStorageType(location.getStorageType());
        response.setTemperatureZone(location.getTemperatureZone());
        response.setActive(location.isActive());
        response.setWarehouseId(location.getWarehouse().getId());
        response.setWarehouseName(location.getWarehouse().getName());
        return response;
    }
}
