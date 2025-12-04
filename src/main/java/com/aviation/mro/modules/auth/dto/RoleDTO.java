package com.aviation.mro.modules.auth.dto;

import com.aviation.mro.modules.auth.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
    private Long id;
    private String name;
    private String displayName;
    private String description;
    private boolean system;
    private LocalDateTime createdAt;
    private Set<PermissionDTO> permissions;

    // Constructor از Entity
    public RoleDTO(Role role) {
        this.id = role.getId();
        this.name = role.getName();
        this.displayName = role.getDisplayName();
        this.description = role.getDescription();
        this.system = role.isSystem();
        this.createdAt = role.getCreatedAt();

        // تبدیل Permissionها به DTO
        if (role.getPermissions() != null) {
            this.permissions = role.getPermissions().stream()
                    .map(PermissionDTO::new)
                    .collect(Collectors.toSet());
        }
    }
}