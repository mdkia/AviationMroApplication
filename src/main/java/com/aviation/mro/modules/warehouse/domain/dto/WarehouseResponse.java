package com.aviation.mro.modules.warehouse.domain.dto;

import com.aviation.mro.modules.warehouse.domain.enums.WarehouseType;
import com.aviation.mro.modules.warehouse.domain.model.Warehouse;
import java.time.LocalDateTime;

public class WarehouseResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String location;
    private WarehouseType type;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer storageLocationCount;

    // Constructors
    public WarehouseResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Integer getStorageLocationCount() { return storageLocationCount; }
    public void setStorageLocationCount(Integer storageLocationCount) { this.storageLocationCount = storageLocationCount; }
}