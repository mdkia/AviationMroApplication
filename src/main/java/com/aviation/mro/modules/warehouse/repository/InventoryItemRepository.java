package com.aviation.mro.modules.warehouse.repository;

import com.aviation.mro.modules.parts.domain.model.AircraftPart;
import com.aviation.mro.modules.warehouse.domain.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    Optional<InventoryItem> findByPartAndStorageLocation(AircraftPart part, StorageLocation storageLocation);

    List<InventoryItem> findByPart(AircraftPart part);
    List<InventoryItem> findByStorageLocation(StorageLocation storageLocation);

    @Query("SELECT ii FROM InventoryItem ii WHERE ii.part.id = :partId")
    List<InventoryItem> findByPartId(@Param("partId") Long partId);

    @Query("SELECT ii FROM InventoryItem ii WHERE ii.storageLocation.warehouse.id = :warehouseId")
    List<InventoryItem> findByWarehouseId(@Param("warehouseId") Long warehouseId);

    @Query("SELECT ii FROM InventoryItem ii WHERE ii.quantityAvailable > 0")
    List<InventoryItem> findAvailableItems();

    @Query("SELECT ii FROM InventoryItem ii WHERE ii.expirationDate < :date")
    List<InventoryItem> findExpiringItems(@Param("date") LocalDate date);

    @Query("SELECT SUM(ii.quantityOnHand * ii.unitCost) FROM InventoryItem ii WHERE ii.unitCost IS NOT NULL")
    Double calculateTotalInventoryValue();

    @Query("SELECT ii FROM InventoryItem ii WHERE ii.quantityOnHand <= :threshold")
    List<InventoryItem> findLowStockItems(@Param("threshold") Integer threshold);
}
