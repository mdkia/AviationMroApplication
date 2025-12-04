package com.aviation.mro.modules.auth.dto;

import com.aviation.mro.modules.auth.model.Permission;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO {
    private Long id;
    private String name;
    private String module;
    private String description;
    private LocalDateTime createdAt;

    public PermissionDTO(Permission permission) {
        this.id = permission.getId();
        this.name = permission.getName();
        this.module = permission.getModule();
        this.description = permission.getDescription();
        this.createdAt = permission.getCreatedAt();
    }
}

