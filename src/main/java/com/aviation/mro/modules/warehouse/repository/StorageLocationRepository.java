package com.aviation.mro.modules.warehouse.repository;

import com.aviation.mro.modules.warehouse.domain.model.StorageLocation;
import com.aviation.mro.modules.warehouse.domain.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StorageLocationRepository extends JpaRepository<StorageLocation, Long> {
    Optional<StorageLocation> findByLocationCode(String locationCode);
    List<StorageLocation> findByWarehouseAndActiveTrue(Warehouse warehouse);
    List<StorageLocation> findByWarehouse(Warehouse warehouse);

    @Query("SELECT sl FROM StorageLocation sl WHERE sl.warehouse.id = :warehouseId AND sl.active = true")
    List<StorageLocation> findActiveByWarehouseId(@Param("warehouseId") Long warehouseId);

    @Query("SELECT sl FROM StorageLocation sl WHERE sl.locationCode LIKE %:keyword% OR sl.name LIKE %:keyword%")
    List<StorageLocation> searchByLocationCodeOrName(@Param("keyword") String keyword);

    boolean existsByLocationCode(String locationCode);
}
