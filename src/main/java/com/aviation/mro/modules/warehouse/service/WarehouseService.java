package com.aviation.mro.modules.warehouse.service;


import com.aviation.mro.modules.warehouse.domain.dto.WarehouseRequest;
import com.aviation.mro.modules.warehouse.domain.dto.WarehouseResponse;
import com.aviation.mro.modules.warehouse.domain.enums.WarehouseType;
import com.aviation.mro.modules.warehouse.domain.model.Warehouse;
import com.aviation.mro.modules.warehouse.repository.WarehouseRepository;
import com.aviation.mro.modules.warehouse.repository.StorageLocationRepository;
import com.aviation.mro.shared.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final StorageLocationRepository storageLocationRepository;

    public WarehouseService(WarehouseRepository warehouseRepository,
                            StorageLocationRepository storageLocationRepository) {
        this.warehouseRepository = warehouseRepository;
        this.storageLocationRepository = storageLocationRepository;
    }

    public List<WarehouseResponse> getAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<WarehouseResponse> getActiveWarehouses() {
        return warehouseRepository.findByActiveTrue().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public WarehouseResponse getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Warehouse not found with id: " + id));
        return convertToResponse(warehouse);
    }

    public WarehouseResponse createWarehouse(WarehouseRequest request) {
        if (warehouseRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Warehouse code already exists: " + request.getCode());
        }

        Warehouse warehouse = new Warehouse();
        warehouse.setCode(request.getCode());
        warehouse.setName(request.getName());
        warehouse.setDescription(request.getDescription());
        warehouse.setLocation(request.getLocation());
        warehouse.setType(request.getType() != null ? request.getType() : WarehouseType.MAIN);

        Warehouse saved = warehouseRepository.save(warehouse);
        return convertToResponse(saved);
    }

    public WarehouseResponse updateWarehouse(Long id, WarehouseRequest request) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Warehouse not found with id: " + id));

        // بررسی یکتایی کد (اگر تغییر کرده)
        if (!warehouse.getCode().equals(request.getCode()) &&
                warehouseRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Warehouse code already exists: " + request.getCode());
        }

        warehouse.setCode(request.getCode());
        warehouse.setName(request.getName());
        warehouse.setDescription(request.getDescription());
        warehouse.setLocation(request.getLocation());
        if (request.getType() != null) {
            warehouse.setType(request.getType());
        }

        Warehouse updated = warehouseRepository.save(warehouse);
        return convertToResponse(updated);
    }

    public void deleteWarehouse(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Warehouse not found with id: " + id));

        // حذف منطقی
        warehouse.setActive(false);
        warehouseRepository.save(warehouse);
    }

    public void activateWarehouse(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Warehouse not found with id: " + id));

        warehouse.setActive(true);
        warehouseRepository.save(warehouse);
    }

    public List<WarehouseResponse> searchWarehouses(String keyword) {
        return warehouseRepository.searchByNameOrCode(keyword).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private WarehouseResponse convertToResponse(Warehouse warehouse) {
        WarehouseResponse response = new WarehouseResponse();
        response.setId(warehouse.getId());
        response.setCode(warehouse.getCode());
        response.setName(warehouse.getName());
        response.setDescription(warehouse.getDescription());
        response.setLocation(warehouse.getLocation());
        response.setType(warehouse.getType());
        response.setActive(warehouse.isActive());
        response.setCreatedAt(warehouse.getCreatedAt());
        response.setUpdatedAt(warehouse.getUpdatedAt());

        // شمارش مکان‌های ذخیره‌سازی فعال
        int locationCount = storageLocationRepository.findActiveByWarehouseId(warehouse.getId()).size();
        response.setStorageLocationCount(locationCount);

        return response;
    }
}