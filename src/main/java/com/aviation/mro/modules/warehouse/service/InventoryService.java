package com.aviation.mro.modules.warehouse.service;


import com.aviation.mro.modules.parts.domain.model.AircraftPart;
import com.aviation.mro.modules.parts.repository.AircraftPartRepository;
import com.aviation.mro.modules.warehouse.domain.dto.InventoryItemRequest;
import com.aviation.mro.modules.warehouse.domain.dto.InventoryItemResponse;
import com.aviation.mro.modules.warehouse.domain.model.InventoryItem;
import com.aviation.mro.modules.warehouse.domain.model.StorageLocation;
import com.aviation.mro.modules.warehouse.repository.InventoryItemRepository;
import com.aviation.mro.modules.warehouse.repository.StorageLocationRepository;
import com.aviation.mro.shared.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final AircraftPartRepository aircraftPartRepository;
    private final StorageLocationRepository storageLocationRepository;

    public InventoryService(InventoryItemRepository inventoryItemRepository,
                            AircraftPartRepository aircraftPartRepository,
                            StorageLocationRepository storageLocationRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.aircraftPartRepository = aircraftPartRepository;
        this.storageLocationRepository = storageLocationRepository;
    }

    public List<InventoryItemResponse> getAllInventoryItems() {
        return inventoryItemRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<InventoryItemResponse> getInventoryByPart(Long partId) {
        return inventoryItemRepository.findByPartId(partId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<InventoryItemResponse> getInventoryByWarehouse(Long warehouseId) {
        return inventoryItemRepository.findByWarehouseId(warehouseId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<InventoryItemResponse> getAvailableItems() {
        return inventoryItemRepository.findAvailableItems().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<InventoryItemResponse> getLowStockItems(Integer threshold) {
        return inventoryItemRepository.findLowStockItems(threshold).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<InventoryItemResponse> getExpiringItems() {
        LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(30);
        return inventoryItemRepository.findExpiringItems(thirtyDaysFromNow).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public InventoryItemResponse createInventoryItem(InventoryItemRequest request) {
        AircraftPart part = aircraftPartRepository.findById(request.getPartId())
                .orElseThrow(() -> new NotFoundException("Aircraft part not found with id: " + request.getPartId()));

        StorageLocation storageLocation = storageLocationRepository.findById(request.getStorageLocationId())
                .orElseThrow(() -> new NotFoundException("Storage location not found with id: " + request.getStorageLocationId()));

        // بررسی اینکه آیا این قطعه قبلاً در این مکان ذخیره شده
        inventoryItemRepository.findByPartAndStorageLocation(part, storageLocation)
                .ifPresent(item -> {
                    throw new RuntimeException("Part already exists in this storage location");
                });

        InventoryItem item = new InventoryItem();
        item.setPart(part);
        item.setStorageLocation(storageLocation);
        item.setQuantityOnHand(request.getQuantityOnHand());
        item.setQuantityReserved(request.getQuantityReserved() != null ? request.getQuantityReserved() : 0);
        item.setUnitCost(request.getUnitCost());
        item.setReceiptDate(request.getReceiptDate());
        item.setExpirationDate(request.getExpirationDate());
        item.setBatchNumber(request.getBatchNumber());
        item.setSupplierInfo(request.getSupplierInfo());

        InventoryItem saved = inventoryItemRepository.save(item);
        return convertToResponse(saved);
    }

    public InventoryItemResponse updateInventoryItem(Long id, InventoryItemRequest request) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Inventory item not found with id: " + id));

        StorageLocation storageLocation = storageLocationRepository.findById(request.getStorageLocationId())
                .orElseThrow(() -> new NotFoundException("Storage location not found with id: " + request.getStorageLocationId()));

        // اگر مکان ذخیره‌سازی تغییر کرده، بررسی یکتایی
        if (!item.getStorageLocation().getId().equals(request.getStorageLocationId())) {
            AircraftPart part = item.getPart();
            inventoryItemRepository.findByPartAndStorageLocation(part, storageLocation)
                    .ifPresent(existingItem -> {
                        if (!existingItem.getId().equals(id)) {
                            throw new RuntimeException("Part already exists in the new storage location");
                        }
                    });
        }

        item.setStorageLocation(storageLocation);
        item.setQuantityOnHand(request.getQuantityOnHand());
        item.setQuantityReserved(request.getQuantityReserved() != null ? request.getQuantityReserved() : 0);
        item.setUnitCost(request.getUnitCost());
        item.setReceiptDate(request.getReceiptDate());
        item.setExpirationDate(request.getExpirationDate());
        item.setBatchNumber(request.getBatchNumber());
        item.setSupplierInfo(request.getSupplierInfo());

        InventoryItem updated = inventoryItemRepository.save(item);
        return convertToResponse(updated);
    }

    public void reserveInventory(Long id, Integer quantity) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Inventory item not found with id: " + id));

        if (quantity <= 0) {
            throw new RuntimeException("Reservation quantity must be positive");
        }

        if (item.getQuantityAvailable() < quantity) {
            throw new RuntimeException("Insufficient available quantity. Available: " + item.getQuantityAvailable());
        }

        item.setQuantityReserved(item.getQuantityReserved() + quantity);
        inventoryItemRepository.save(item);
    }

    public void releaseReservation(Long id, Integer quantity) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Inventory item not found with id: " + id));

        if (quantity <= 0) {
            throw new RuntimeException("Release quantity must be positive");
        }

        if (item.getQuantityReserved() < quantity) {
            throw new RuntimeException("Cannot release more than reserved quantity. Reserved: " + item.getQuantityReserved());
        }

        item.setQuantityReserved(item.getQuantityReserved() - quantity);
        inventoryItemRepository.save(item);
    }

    public Double calculateTotalInventoryValue() {
        Double totalValue = inventoryItemRepository.calculateTotalInventoryValue();
        return totalValue != null ? totalValue : 0.0;
    }

    private InventoryItemResponse convertToResponse(InventoryItem item) {
        InventoryItemResponse response = new InventoryItemResponse();
        response.setId(item.getId());
        response.setPartId(item.getPart().getId());
        response.setPartNumber(item.getPart().getPartNumber());
        response.setPartName(item.getPart().getPartName());
        response.setStorageLocationId(item.getStorageLocation().getId());
        response.setStorageLocationCode(item.getStorageLocation().getLocationCode());
        response.setWarehouseId(item.getStorageLocation().getWarehouse().getId());
        response.setWarehouseName(item.getStorageLocation().getWarehouse().getName());
        response.setQuantityOnHand(item.getQuantityOnHand());
        response.setQuantityReserved(item.getQuantityReserved());
        response.setQuantityAvailable(item.getQuantityAvailable());
        response.setUnitCost(item.getUnitCost());
        response.setReceiptDate(item.getReceiptDate());
        response.setExpirationDate(item.getExpirationDate());
        response.setBatchNumber(item.getBatchNumber());
        response.setSupplierInfo(item.getSupplierInfo());
        response.setStatus(item.getStatus());
        response.setLastCountDate(item.getLastCountDate());
        response.setLastCountBy(item.getLastCountBy());
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());
        return response;
    }
}
