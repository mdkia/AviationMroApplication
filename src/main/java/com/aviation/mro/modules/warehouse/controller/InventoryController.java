package com.aviation.mro.modules.warehouse.controller;


import com.aviation.mro.modules.warehouse.domain.dto.InventoryItemRequest;
import com.aviation.mro.modules.warehouse.domain.dto.InventoryItemResponse;
import com.aviation.mro.modules.warehouse.service.InventoryService;
import com.aviation.mro.shared.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse/inventory")
@PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'TECHNICIAN', 'INSPECTOR')")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<InventoryItemResponse>> getAllInventoryItems() {
        return ResponseEntity.ok(inventoryService.getAllInventoryItems());
    }

    @GetMapping("/part/{partId}")
    public ResponseEntity<List<InventoryItemResponse>> getInventoryByPart(@PathVariable Long partId) {
        return ResponseEntity.ok(inventoryService.getInventoryByPart(partId));
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<InventoryItemResponse>> getInventoryByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(inventoryService.getInventoryByWarehouse(warehouseId));
    }

    @GetMapping("/available")
    public ResponseEntity<List<InventoryItemResponse>> getAvailableItems() {
        return ResponseEntity.ok(inventoryService.getAvailableItems());
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryItemResponse>> getLowStockItems(@RequestParam(defaultValue = "5") Integer threshold) {
        return ResponseEntity.ok(inventoryService.getLowStockItems(threshold));
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<InventoryItemResponse>> getExpiringItems() {
        return ResponseEntity.ok(inventoryService.getExpiringItems());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<InventoryItemResponse> createInventoryItem(@Valid @RequestBody InventoryItemRequest request) {
        return ResponseEntity.ok(inventoryService.createInventoryItem(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<InventoryItemResponse> updateInventoryItem(
            @PathVariable Long id,
            @Valid @RequestBody InventoryItemRequest request) {
        return ResponseEntity.ok(inventoryService.updateInventoryItem(id, request));
    }

    @PatchMapping("/{id}/reserve")
    public ResponseEntity<ApiResponse> reserveInventory(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        inventoryService.reserveInventory(id, quantity);
        return ResponseEntity.ok(ApiResponse.success("Inventory reserved successfully"));
    }

    @PatchMapping("/{id}/release")
    public ResponseEntity<ApiResponse> releaseReservation(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        inventoryService.releaseReservation(id, quantity);
        return ResponseEntity.ok(ApiResponse.success("Reservation released successfully"));
    }

    @GetMapping("/value")
    public ResponseEntity<ApiResponse> getTotalInventoryValue() {
        Double totalValue = inventoryService.calculateTotalInventoryValue();
        return ResponseEntity.ok(ApiResponse.success("Total inventory value calculated", totalValue));
    }
}