package com.aviation.mro.modules.warehouse.domain.dto;

import com.aviation.mro.modules.warehouse.domain.enums.WarehouseType;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
}