package com.aviation.mro.modules.auth.controller;

import com.aviation.mro.modules.auth.dto.*;
import com.aviation.mro.modules.auth.model.User;
import com.aviation.mro.modules.auth.service.UserManagementService;
import com.aviation.mro.shared.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasAuthority('MANAGE_USERS')")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserManagementService userManagementService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userManagementService.getAllUsers());
    }

    @GetMapping("/active")
    public ResponseEntity<List<UserResponse>> getActiveUsers() {
        return ResponseEntity.ok(userManagementService.getActiveUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userManagementService.getUserById(userId));
    }

    @PostMapping("/{userId}/roles")
    public ResponseEntity<ApiResponse> assignRole(
            @PathVariable Long userId,
            @Valid @RequestBody AssignRoleRequest request) {
        userManagementService.assignRoleToUser(userId, request.roleId());
        return ResponseEntity.ok(ApiResponse.success("Role assigned successfully"));
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<ApiResponse> removeRole(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        userManagementService.removeRoleFromUser(userId, roleId);
        return ResponseEntity.ok(ApiResponse.success("Role removed successfully"));
    }

    @PutMapping("/{userId}/roles")
    public ResponseEntity<ApiResponse> updateUserRoles(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRolesRequest request) {
        userManagementService.updateUserRoles(userId, request.roleIds());
        return ResponseEntity.ok(ApiResponse.success("User roles updated successfully"));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> softDeleteUser(
            @PathVariable Long userId,
            @Valid @RequestBody DeleteUserRequest request) {
        userManagementService.softDeleteUser(userId, request.getDeletedBy());
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }

    @PostMapping("/{userId}/restore")
    public ResponseEntity<ApiResponse> restoreUser(@PathVariable Long userId) {
        userManagementService.restoreUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User restored successfully"));
    }

    @PatchMapping("/{userId}/status")
    public ResponseEntity<ApiResponse> toggleUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody ToggleUserStatusRequest request) {
        userManagementService.toggleUserStatus(userId, request.isEnabled());
        String msg = request.isEnabled() ? "User activated" : "User deactivated";
        return ResponseEntity.ok(ApiResponse.success(msg));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String keyword) {
        return ResponseEntity.ok(userManagementService.searchUsers(keyword));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userManagementService.registerUser(
                request.username(),
                request.email(),
                request.password(),
                request.firstName(),
                request.lastName()
        );
        UserResponse response = userManagementService.convertToResponse(user);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
    }
}