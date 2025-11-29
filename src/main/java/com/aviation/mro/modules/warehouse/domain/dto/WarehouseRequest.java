package com.aviation.mro.modules.warehouse.domain.dto;

import com.aviation.mro.modules.warehouse.domain.enums.WarehouseType;
import com.aviation.mro.modules.warehouse.domain.model.Warehouse;
import jakarta.validation.constraints.NotBlank;

public class WarehouseRequest {
    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String description;
    private String location;
    private WarehouseType type;

    // Constructors
    public WarehouseRequest() {}

    public WarehouseRequest(String code, String name, String location) {
        this.code = code;
        this.name = name;
        this.location = location;
    }

    // Getters and Setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public WarehouseType getType() { return type; }
    public void setType(WarehouseType type) { this.type = type; }
}
