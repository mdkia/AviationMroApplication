package com.aviation.mro.modules.auth.service;

import com.aviation.mro.modules.auth.dto.UserResponse;
import com.aviation.mro.modules.auth.model.User;
import com.aviation.mro.modules.auth.repository.UserRepository;
import com.aviation.mro.shared.common.ApiResponse;
import com.aviation.mro.shared.exceptions.InvalidRoleException;
import com.aviation.mro.shared.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserManagementService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // لیست نقش‌های معتبر در سیستم
    private static final Set<String> VALID_ROLES = Set.of(
            "ADMIN", "TECHNICIAN", "INSPECTOR", "WAREHOUSE_MANAGER",
            "SALES_MANAGER", "ACCOUNTANT", "READ_ONLY"
    );

    public UserManagementService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getActiveUsers() {
        return userRepository.findByDeletedFalse().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));
        return convertToResponse(user);
    }

    public void updateUserRoles(Long userId, Set<String> roles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        // اعتبارسنجی نقش‌ها
        validateRoles(roles);

        user.getRoles().clear();
        user.getRoles().addAll(roles);

        userRepository.save(user);
    }

    public void addRoleToUser(Long userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        // اعتبارسنجی نقش
        validateRole(role);

        user.getRoles().add(role.toUpperCase());
        userRepository.save(user);
    }

    public void removeRoleFromUser(Long userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        user.getRoles().remove(role.toUpperCase());
        userRepository.save(user);
    }

    public void softDeleteUser(Long userId, String deletedBy) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        user.setDeleted(true);
        user.setDeletedBy(deletedBy);
        user.setEnabled(false);

        userRepository.save(user);
    }

    public void restoreUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        user.setDeleted(false);
        user.setDeletedBy(null);
        user.setEnabled(true);

        userRepository.save(user);
    }

    public void toggleUserStatus(Long userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        user.setEnabled(enabled);
        userRepository.save(user);
    }

    public List<UserResponse> searchUsers(String keyword) {
        // روش ۱: استفاده از متد Spring Data JPA
        List<User> users = userRepository.findByUsernameContainingOrEmailContaining(keyword, keyword);

        // روش ۲: یا استفاده از @Query (اگر می‌خواهید نام و نام خانوادگی هم جستجو شود)
        // List<User> users = userRepository.searchUsers(keyword);

        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getUsersByRole(String role) {
        return userRepository.findByRolesContaining(role).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEnabled(user.isEnabled());
        response.setDeleted(user.isDeleted());
        response.setDeletedAt(user.getDeletedAt());
        response.setDeletedBy(user.getDeletedBy());
        response.setRoles(user.getRoles());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }

    private void validateRoles(Set<String> roles) {
        for (String role : roles) {
            validateRole(role);
        }
    }

    private void validateRole(String role) {
        if (!VALID_ROLES.contains(role.toUpperCase())) {
            throw InvalidRoleException.withRole(role);
        }
    }

    public ApiResponse registerUser(String username, String email, String password,
                                    String firstName, String lastName) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username is already taken!");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already in use!");
        }

        User user = new User(username, email, passwordEncoder.encode(password), firstName, lastName);
        user.getRoles().add("TECHNICIAN");

        userRepository.save(user);

        return ApiResponse.success("User registered successfully");
    }
}