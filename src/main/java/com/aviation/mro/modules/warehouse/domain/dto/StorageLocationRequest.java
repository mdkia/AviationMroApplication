package com.aviation.mro.modules.warehouse.domain.dto;

import com.aviation.mro.modules.warehouse.domain.enums.StorageType;
import com.aviation.mro.modules.warehouse.domain.enums.TemperatureZone;
import com.aviation.mro.modules.warehouse.domain.model.StorageLocation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StorageLocationRequest {
    @NotBlank
    private String locationCode;

    @NotBlank
    private String name;

    private String description;
    private Double maxWeight;
    private Double maxVolume;
    private StorageType storageType;
    private TemperatureZone temperatureZone;

    @NotNull
    private Long warehouseId;

    // Constructors
    public StorageLocationRequest() {}

    // Getters and Setters
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
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
}
