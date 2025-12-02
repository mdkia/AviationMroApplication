// InventoryReportHelper.java
package com.aviation.mro.modules.reporting.service;

import com.aviation.mro.modules.warehouse.domain.model.InventoryItem;
import com.aviation.mro.modules.warehouse.domain.model.StorageLocation;
import com.aviation.mro.modules.warehouse.repository.InventoryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InventoryReportHelper {

    private final InventoryItemRepository inventoryItemRepository;

    public long countTotalParts() {
        return inventoryItemRepository.count();
    }

    public long countLowStock() {
        return inventoryItemRepository.findAll().stream()
                .filter(item -> item.getQuantityAvailable() != null && item.getQuantityAvailable() < 10)
                .count();
    }

    public long countCriticalStock() {
        return inventoryItemRepository.findAll().stream()
                .filter(item -> item.getQuantityAvailable() != null && item.getQuantityAvailable() <= 5)
                .count();
    }

    public List<Map<String, Object>> getInventoryReport(String partNumber, String status) {
        List<InventoryItem> items = inventoryItemRepository.findAll();

        return items.stream()
                .filter(item -> item.getPart() != null)
                .filter(item -> partNumber == null ||
                        item.getPart().getPartNumber() != null &&
                                item.getPart().getPartNumber().toLowerCase().contains(partNumber.toLowerCase().trim()))
                .filter(item -> status == null ||
                        item.getStatus() != null &&
                                item.getStatus().name().equalsIgnoreCase(status.trim()))
                .map(item -> {
                    StorageLocation location = item.getStorageLocation();
                    String locationCode = location != null && location.getLocationCode() != null
                            ? location.getLocationCode()
                            : "نامشخص";

                    Integer qtyAvailable = item.getQuantityAvailable() != null ? item.getQuantityAvailable() : 0;

                    Map<String, Object> map = new HashMap<>();
                    map.put("inventoryItemId", item.getId());
                    map.put("partNumber", item.getPart().getPartNumber() != null ? item.getPart().getPartNumber() : "");
                    map.put("partName", item.getPart().getPartName() != null ? item.getPart().getPartName() : "نامشخص");
                    map.put("serialNumber", item.getPart().getSerialNumber() != null ? item.getPart().getSerialNumber() : "");
                    map.put("batchNumber", item.getPart().getBatchNumber() != null ? item.getPart().getBatchNumber() : "");
                    map.put("quantityOnHand", item.getQuantityOnHand() != null ? item.getQuantityOnHand() : 0);
                    map.put("quantityReserved", item.getQuantityReserved() != null ? item.getQuantityReserved() : 0);
                    map.put("quantityAvailable", qtyAvailable);
                    map.put("locationCode", locationCode);
                    map.put("status", item.getStatus() != null ? item.getStatus().name() : "نامشخص");
                    map.put("statusInPersian", getPersianStatus(item.getStatus()));
                    map.put("lastUpdated", item.getUpdatedAt());
                    map.put("isLowStock", qtyAvailable < 10);
                    map.put("isCritical", qtyAvailable <= 5);

                    return map;
                })
                .toList();
    }

    private String getPersianStatus(com.aviation.mro.modules.warehouse.domain.enums.InventoryStatus status) {
        if (status == null) return "نامشخص";
        return switch (status) {
            case ACTIVE -> "فعال";
            case QUARANTINED -> "قرنطینه";
            case HOLD -> "مسدود";
            case EXPIRED -> "منقضی شده";
            case SCRAPPED -> "اسقاط شده";
        };
    }
}