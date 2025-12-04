package com.aviation.mro.modules.auth.controller;

import com.aviation.mro.modules.auth.dto.CreatePermissionRequest;
import com.aviation.mro.modules.auth.dto.CreateRoleRequest;
import com.aviation.mro.modules.auth.model.Permission;
import com.aviation.mro.modules.auth.model.Role;
import com.aviation.mro.modules.auth.service.PermissionService;
import com.aviation.mro.modules.auth.service.RoleService;
import com.aviation.mro.shared.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Role & Permission Management", description = "مدیریت نقش‌ها و دسترسی‌ها")
@PreAuthorize("hasAnyAuthority('MANAGE_ROLES', 'MANAGE_SYSTEM')")
public class RoleManagementController {
    private final RoleService roleService;
    private final PermissionService permissionService;

    public RoleManagementController(RoleService roleService, PermissionService permissionService) {
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    @PostMapping("/roles")
    @Operation(summary = "ایجاد نقش جدید")
    public ResponseEntity<ApiResponse> createRole(
            @Valid @RequestBody CreateRoleRequest request) {
        Role role = roleService.createRole(
                request.getName(),
                request.getDisplayName(),
                request.getDescription()
        );
        return ResponseEntity.ok(ApiResponse.success("Role created successfully", role));
    }

    @GetMapping("/roles")
    @Operation(summary = "دریافت همه نقش‌ها")
    public ResponseEntity<ApiResponse> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.success("Roles retrieved successfully", roles));
    }

    @PutMapping("/roles/{id}/permissions")
    @Operation(summary = "به‌روزرسانی دسترسی‌های نقش")
    public ResponseEntity<ApiResponse> updateRolePermissions(
            @PathVariable Long id,
            @Valid @RequestBody Set<String> permissionNames) {
        Role role = roleService.updateRolePermissions(id, permissionNames);
        return ResponseEntity.ok(ApiResponse.success("Role permissions updated", role));
    }

    @DeleteMapping("/roles/{id}")
    @Operation(summary = "حذف نقش")
    public ResponseEntity<ApiResponse> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success("Role deleted successfully"));
    }

    @GetMapping("/permissions")
    @Operation(summary = "دریافت همه دسترسی‌ها")
    public ResponseEntity<ApiResponse> getAllPermissions() {
        List<Permission> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(ApiResponse.success("Permissions retrieved successfully", permissions));
    }

    @PostMapping("/permissions")
    @Operation(summary = "ایجاد دسترسی جدید")
    public ResponseEntity<ApiResponse> createPermission(
            @Valid @RequestBody CreatePermissionRequest request) {
        Permission permission = permissionService.createPermission(
                request.getName(),
                request.getModule(),
                request.getDescription()
        );
        return ResponseEntity.ok(ApiResponse.success("Permission created successfully", permission));
    }
}



