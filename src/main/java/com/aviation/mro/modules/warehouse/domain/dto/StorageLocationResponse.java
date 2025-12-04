package com.aviation.mro.modules.warehouse.domain.dto;

import com.aviation.mro.modules.warehouse.domain.enums.StorageType;
import com.aviation.mro.modules.warehouse.domain.enums.TemperatureZone;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StorageLocationResponse {
    private Long id;
    private String locationCode;
    private String name;
    private String description;
    private Double maxWeight;
    private Double maxVolume;
    private StorageType storageType;
    private TemperatureZone temperatureZone;
    private boolean active;
    private Long warehouseId;
    private String warehouseName;
}
