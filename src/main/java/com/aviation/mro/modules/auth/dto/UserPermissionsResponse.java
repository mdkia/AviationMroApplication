package com.aviation.mro.modules.auth.dto;


import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class UserPermissionsResponse {
    // اطلاعات کاربر
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private boolean enabled;
    private boolean deleted;

    // دسترسی‌ها
    private List<String> roles;                    // لیست نام نقش‌ها
    private List<String> permissions;              // لیست permissionها
    private List<String> authorities;              // لیست کامل (roles + permissions)

    // جزئیات بیشتر
    private List<Map<String, Object>> rolesDetails; // جزئیات کامل هر نقش

    // permissionهای مهم (برای راحتی)
    private boolean hasManageSystem;
    private boolean hasManageUsers;
    private boolean hasManageRoles;
    private boolean hasManagePermissions;

    // متدهای کمکی
    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }

    public boolean hasAnyPermission(List<String> requiredPermissions) {
        if (permissions == null || requiredPermissions == null) {
            return false;
        }
        return permissions.stream()
                .anyMatch(requiredPermissions::contains);
    }

    public boolean hasAllPermissions(List<String> requiredPermissions) {
        if (permissions == null || requiredPermissions == null) {
            return false;
        }
        return permissions.containsAll(requiredPermissions);
    }

    public boolean hasRole(String roleName) {
        return roles != null && roles.contains(roleName);
    }
}
