package com.aviation.mro.modules.warehouse.repository;

import com.aviation.mro.modules.warehouse.domain.enums.WarehouseType;
import com.aviation.mro.modules.warehouse.domain.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Optional<Warehouse> findByCode(String code);
    List<Warehouse> findByActiveTrue();
    List<Warehouse> findByType(WarehouseType type);

    @Query("SELECT w FROM Warehouse w WHERE w.name LIKE %:keyword% OR w.code LIKE %:keyword%")
    List<Warehouse> searchByNameOrCode(@Param("keyword") String keyword);

    boolean existsByCode(String code);
}
