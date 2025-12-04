package com.aviation.mro.modules.auth.dto;

import com.aviation.mro.modules.auth.model.Permission;
import com.aviation.mro.modules.auth.model.Role;
import com.aviation.mro.modules.auth.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private boolean deleted;
    private LocalDateTime deletedAt;
    private String deletedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // انتخاب کنید: Set<RoleDTO> یا Set<String>
    private Set<RoleDTO> roles = new HashSet<>();
    private Set<String> permissions = new HashSet<>();

    // Constructor از Entity User
    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.enabled = user.isEnabled();
        this.deleted = user.isDeleted();
        this.deletedAt = user.getDeletedAt();
        this.deletedBy = user.getDeletedBy();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();

        if (user.getRoles() != null) {
            // اگر Set<RoleDTO> دارید
            this.roles = user.getRoles().stream()
                    .map(RoleDTO::new)
                    .collect(Collectors.toSet());

            // جمع‌آوری تمام permissionها
            this.permissions = user.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .map(Permission::getName)
                    .collect(Collectors.toSet());
        }
    }
}