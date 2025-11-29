package com.aviation.mro.modules.auth.controller;

import com.aviation.mro.modules.auth.dto.*;
import com.aviation.mro.modules.auth.service.UserManagementService;
import com.aviation.mro.shared.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    private final UserManagementService userManagementService;

    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userManagementService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/active")
    public ResponseEntity<List<UserResponse>> getActiveUsers() {
        List<UserResponse> users = userManagementService.getActiveUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        UserResponse user = userManagementService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}/roles")
    public ResponseEntity<ApiResponse> updateUserRoles(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateRolesRequest request) {

        userManagementService.updateUserRoles(userId, request.getRoles());
        return ResponseEntity.ok(ApiResponse.success("User roles updated successfully"));
    }

    @PostMapping("/{userId}/roles")
    public ResponseEntity<ApiResponse> addRoleToUser(
            @PathVariable Long userId,
            @Valid @RequestBody AddRoleRequest request) {

        userManagementService.addRoleToUser(userId, request.getRole());
        return ResponseEntity.ok(ApiResponse.success("Role added to user successfully"));
    }

    @DeleteMapping("/{userId}/roles/{role}")
    public ResponseEntity<ApiResponse> removeRoleFromUser(
            @PathVariable Long userId,
            @PathVariable String role) {

        userManagementService.removeRoleFromUser(userId, role);
        return ResponseEntity.ok(ApiResponse.success("Role removed from user successfully"));
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
        String message = request.isEnabled() ? "User activated successfully" : "User deactivated successfully";
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(
            @RequestParam String keyword) {

        List<UserResponse> users = userManagementService.searchUsers(keyword);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/by-role/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable String role) {
        List<UserResponse> users = userManagementService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody Map<String, String> registerRequest) {
        String username = registerRequest.get("username");
        String email = registerRequest.get("email");
        String password = registerRequest.get("password");
        String firstName = registerRequest.get("firstName");
        String lastName = registerRequest.get("lastName");

        if (username == null || email == null || password == null || firstName == null || lastName == null) {
            throw new RuntimeException("All fields are required");
        }

        ApiResponse response = userManagementService.registerUser(username, email, password, firstName, lastName);
        return ResponseEntity.ok(response);
    }


}