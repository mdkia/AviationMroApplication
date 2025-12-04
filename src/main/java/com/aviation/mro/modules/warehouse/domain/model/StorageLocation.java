package com.aviation.mro.modules.warehouse.domain.model;

import com.aviation.mro.modules.warehouse.domain.enums.StorageType;
import com.aviation.mro.modules.warehouse.domain.enums.TemperatureZone;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "storage_locations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StorageLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @NotBlank
    private String locationCode; // فرمت: Aisle-Row-Shelf-Level (مثلاً: A-01-B-02)

    @Nationalized
    @NotBlank
    private String name;

    @Nationalized
    private String description;
    private Double maxWeight; // حداکثر وزن قابل تحمل (کیلوگرم)
    private Double maxVolume; // حداکثر حجم (متر مکعب)

    @Enumerated(EnumType.STRING)
    private StorageType storageType = StorageType.SHELF;

    @Enumerated(EnumType.STRING)
    private TemperatureZone temperatureZone = TemperatureZone.AMBIENT;

    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    public StorageLocation(String locationCode, String name, Warehouse warehouse) {
        this.locationCode = locationCode;
        this.name = name;
        this.warehouse = warehouse;
    }
}