package com.aviation.mro.modules.auth.controller;

import com.aviation.mro.modules.auth.dto.UserPermissionsResponse;
import com.aviation.mro.modules.auth.model.Permission;
import com.aviation.mro.modules.auth.model.Role;
import com.aviation.mro.modules.auth.model.User;
import com.aviation.mro.modules.auth.repository.UserRepository;
import com.aviation.mro.shared.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication & Authorization", description = "احراز هویت و مدیریت دسترسی‌ها")
public class UserPermissionsController {

    private final UserRepository userRepository;

    public UserPermissionsController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * دریافت تمام دسترسی‌های کاربر جاری (خود کاربر)
     */
    @GetMapping("/my-permissions")
    @Operation(summary = "دریافت دسترسی‌های کاربر جاری")
    public ResponseEntity<UserPermissionsResponse> getMyPermissions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserPermissionsResponse response = new UserPermissionsResponse();
        response.setUsername(authentication.getName());

        // دریافت authorities از Spring Security
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        response.setAuthorities(authorities);

        // جدا کردن roles و permissions
        List<String> roles = authorities.stream()
                .filter(auth -> auth.startsWith("ROLE_"))
                .map(role -> role.substring(5)) // حذف پیشوند ROLE_
                .collect(Collectors.toList());
        response.setRoles(roles);

        List<String> permissions = authorities.stream()
                .filter(auth -> !auth.startsWith("ROLE_"))
                .collect(Collectors.toList());
        response.setPermissions(permissions);

        response.setHasManageSystem(permissions.contains("MANAGE_SYSTEM"));

        return ResponseEntity.ok(response);
    }

    /**
     * دریافت دسترسی‌های کاربر دیگر (برای ادمین)
     */
    @GetMapping("/users/{userId}/permissions")
    @Operation(summary = "دریافت دسترسی‌های یک کاربر خاص")
    @PreAuthorize("hasAuthority('MANAGE_USERS') or hasAuthority('MANAGE_SYSTEM')")
    public ResponseEntity<UserPermissionsResponse> getUserPermissions(@PathVariable Long userId) {
        User user = userRepository.findByIdWithRolesAndPermissions(userId)
                .orElseThrow(() -> ResourceNotFoundException.withResource("User", userId));

        return buildUserPermissionsResponse(user);
    }

    /**
     * دریافت دسترسی‌های کاربر بر اساس نام کاربری
     */
    @GetMapping("/users/by-username/{username}/permissions")
    @Operation(summary = "دریافت دسترسی‌های کاربر بر اساس نام کاربری")
    @PreAuthorize("hasAuthority('MANAGE_USERS') or hasAuthority('MANAGE_SYSTEM')")
    public ResponseEntity<UserPermissionsResponse> getUserPermissionsByUsername(@PathVariable String username) {
        User user = userRepository.findByUsernameWithRolesAndPermissions(username)
                .orElseThrow(() -> ResourceNotFoundException.withResource("username", username));

        return buildUserPermissionsResponse(user);
    }

    /**
     * بررسی اینکه آیا کاربر دسترسی خاصی دارد
     */
    @GetMapping("/check-permission/{permission}")
    @Operation(summary = "بررسی دسترسی کاربر جاری به یک permission خاص")
    public ResponseEntity<Map<String, Object>> checkPermission(@PathVariable String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean hasPermission = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(permission));

        Map<String, Object> response = new HashMap<>();
        response.put("username", authentication.getName());
        response.put("permission", permission);
        response.put("hasPermission", hasPermission);
        response.put("checkedAt", new Date());

        return ResponseEntity.ok(response);
    }

    /**
     * بررسی اینکه آیا کاربر حداقل یکی از دسترسی‌های داده شده را دارد
     */
    @PostMapping("/check-any-permission")
    @Operation(summary = "بررسی دسترسی کاربر جاری به حداقل یکی از permissionهای داده شده")
    public ResponseEntity<Map<String, Object>> checkAnyPermission(@RequestBody List<String> permissions) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<String> userPermissions = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        List<String> matchedPermissions = permissions.stream()
                .filter(userPermissions::contains)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("username", authentication.getName());
        response.put("requestedPermissions", permissions);
        response.put("userPermissions", userPermissions);
        response.put("matchedPermissions", matchedPermissions);
        response.put("hasAny", !matchedPermissions.isEmpty());
        response.put("checkedAt", new Date());

        return ResponseEntity.ok(response);
    }

    /**
     * بررسی اینکه آیا کاربر همه دسترسی‌های داده شده را دارد
     */
    @PostMapping("/check-all-permissions")
    @Operation(summary = "بررسی دسترسی کاربر جاری به همه permissionهای داده شده")
    public ResponseEntity<Map<String, Object>> checkAllPermissions(@RequestBody List<String> permissions) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<String> userPermissions = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        boolean hasAll = permissions.stream()
                .allMatch(userPermissions::contains);

        List<String> missingPermissions = permissions.stream()
                .filter(p -> !userPermissions.contains(p))
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("username", authentication.getName());
        response.put("requestedPermissions", permissions);
        response.put("userPermissions", userPermissions);
        response.put("hasAll", hasAll);
        response.put("missingPermissions", missingPermissions);
        response.put("checkedAt", new Date());

        return ResponseEntity.ok(response);
    }

    /**
     * دریافت لیست permissionهای موجود در سیستم
     */
    @GetMapping("/available-permissions")
    @Operation(summary = "دریافت لیست تمام permissionهای موجود در سیستم")
    @PreAuthorize("hasAuthority('MANAGE_PERMISSIONS') or hasAuthority('MANAGE_SYSTEM')")
    public ResponseEntity<Map<String, Object>> getAvailablePermissions() {
        // این متد نیاز به PermissionRepository دارد
        // فعلاً فقط authorities کاربر جاری را برمی‌گرداند

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> allAuthorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("totalPermissions", allAuthorities.size());
        response.put("permissions", allAuthorities);
        response.put("retrievedAt", new Date());

        return ResponseEntity.ok(response);
    }

    /**
     * متد کمکی برای ساخت response
     */
    private ResponseEntity<UserPermissionsResponse> buildUserPermissionsResponse(User user) {
        UserPermissionsResponse response = new UserPermissionsResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFirstName() + " " + user.getLastName());
        response.setEnabled(user.isEnabled());
        response.setDeleted(user.isDeleted());

        // جمع‌آوری roles
        List<String> roleNames = new ArrayList<>();
        if (user.getRoles() != null) {
            roleNames = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());
        }
        response.setRoles(roleNames);

        // جمع‌آوری permissions
        Set<String> permissionNames = new HashSet<>();
        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> {
                if (role.getPermissions() != null) {
                    permissionNames.addAll(
                            role.getPermissions().stream()
                                    .map(Permission::getName)
                                    .collect(Collectors.toSet())
                    );
                }
            });
        }
        response.setPermissions(new ArrayList<>(permissionNames));

        // authorities ترکیبی از roles و permissions
        List<String> authorities = new ArrayList<>();
        roleNames.forEach(role -> authorities.add("ROLE_" + role));
        authorities.addAll(permissionNames);
        response.setAuthorities(authorities);

        response.setHasManageSystem(permissionNames.contains("MANAGE_SYSTEM"));
        response.setHasManageUsers(permissionNames.contains("MANAGE_USERS"));
        response.setHasManageRoles(permissionNames.contains("MANAGE_ROLES"));

        // اطلاعات نقش‌ها با جزئیات
        List<Map<String, Object>> rolesDetails = new ArrayList<>();
        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> {
                Map<String, Object> roleDetail = new HashMap<>();
                roleDetail.put("id", role.getId());
                roleDetail.put("name", role.getName());
                roleDetail.put("displayName", role.getDisplayName());
                roleDetail.put("description", role.getDescription());
                roleDetail.put("system", role.isSystem());

                if (role.getPermissions() != null) {
                    List<String> rolePermissions = role.getPermissions().stream()
                            .map(Permission::getName)
                            .collect(Collectors.toList());
                    roleDetail.put("permissions", rolePermissions);
                }

                rolesDetails.add(roleDetail);
            });
        }
        response.setRolesDetails(rolesDetails);

        return ResponseEntity.ok(response);
    }
}