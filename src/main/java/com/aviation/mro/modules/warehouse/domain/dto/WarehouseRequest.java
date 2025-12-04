package com.aviation.mro.modules.warehouse.domain.dto;

import com.aviation.mro.modules.warehouse.domain.enums.WarehouseType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseRequest {
    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String description;
    private String location;
    private WarehouseType type;

    public WarehouseRequest(String code, String name, String location) {
        this.code = code;
        this.name = name;
        this.location = location;
    }
}
