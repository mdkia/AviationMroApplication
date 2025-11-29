package com.aviation.mro.modules.warehouse.domain.model;

import com.aviation.mro.modules.warehouse.domain.enums.StorageType;
import com.aviation.mro.modules.warehouse.domain.enums.TemperatureZone;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "storage_locations")
public class StorageLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String locationCode; // فرمت: Aisle-Row-Shelf-Level (مثلاً: A-01-B-02)

    @NotBlank
    private String name;

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

    // Constructors
    public StorageLocation() {}

    public StorageLocation(String locationCode, String name, Warehouse warehouse) {
        this.locationCode = locationCode;
        this.name = name;
        this.warehouse = warehouse;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLocationCode() { return locationCode; }
    public void setLocationCode(String locationCode) { this.locationCode = locationCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getMaxWeight() { return maxWeight; }
    public void setMaxWeight(Double maxWeight) { this.maxWeight = maxWeight; }
    public Double getMaxVolume() { return maxVolume; }
    public void setMaxVolume(Double maxVolume) { this.maxVolume = maxVolume; }
    public StorageType getStorageType() { return storageType; }
    public void setStorageType(StorageType storageType) { this.storageType = storageType; }
    public TemperatureZone getTemperatureZone() { return temperatureZone; }
    public void setTemperatureZone(TemperatureZone temperatureZone) { this.temperatureZone = temperatureZone; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Warehouse getWarehouse() { return warehouse; }
    public void setWarehouse(Warehouse warehouse) { this.warehouse = warehouse; }
}