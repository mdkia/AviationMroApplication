package com.aviation.mro.modules.warehouse.domain.dto;

import com.aviation.mro.modules.warehouse.domain.enums.StorageType;
import com.aviation.mro.modules.warehouse.domain.enums.TemperatureZone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
}
