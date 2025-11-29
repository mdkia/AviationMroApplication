package com.aviation.mro.modules.warehouse.controller;

import com.aviation.mro.modules.warehouse.domain.dto.StorageLocationRequest;
import com.aviation.mro.modules.warehouse.domain.dto.StorageLocationResponse;
import com.aviation.mro.modules.warehouse.service.StorageLocationService;
import com.aviation.mro.shared.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse/locations")
@PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'TECHNICIAN', 'INSPECTOR')")
public class StorageLocationController {

    private final StorageLocationService storageLocationService;

    public StorageLocationController(StorageLocationService storageLocationService) {
        this.storageLocationService = storageLocationService;
    }

    @GetMapping
    public ResponseEntity<List<StorageLocationResponse>> getAllStorageLocations() {
        return ResponseEntity.ok(storageLocationService.getAllStorageLocations());
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<StorageLocationResponse>> getStorageLocationsByWarehouse(
            @PathVariable Long warehouseId) {
        return ResponseEntity.ok(storageLocationService.getStorageLocationsByWarehouse(warehouseId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StorageLocationResponse> getStorageLocationById(@PathVariable Long id) {
        return ResponseEntity.ok(storageLocationService.getStorageLocationById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<StorageLocationResponse> createStorageLocation(
            @Valid @RequestBody StorageLocationRequest request) {
        return ResponseEntity.ok(storageLocationService.createStorageLocation(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<StorageLocationResponse> updateStorageLocation(
            @PathVariable Long id,
            @Valid @RequestBody StorageLocationRequest request) {
        return ResponseEntity.ok(storageLocationService.updateStorageLocation(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<ApiResponse> deleteStorageLocation(@PathVariable Long id) {
        storageLocationService.deleteStorageLocation(id);
        return ResponseEntity.ok(ApiResponse.success("Storage location deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<List<StorageLocationResponse>> searchStorageLocations(@RequestParam String keyword) {
        return ResponseEntity.ok(storageLocationService.searchStorageLocations(keyword));
    }
}
