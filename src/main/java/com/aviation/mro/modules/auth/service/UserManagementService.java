package com.aviation.mro.modules.auth.service;

import com.aviation.mro.modules.auth.dto.UserResponse;
import com.aviation.mro.modules.auth.model.Role;
import com.aviation.mro.modules.auth.model.User;
import com.aviation.mro.modules.auth.repository.RoleRepository;
import com.aviation.mro.modules.auth.repository.UserRepository;
import com.aviation.mro.shared.exceptions.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserManagementService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagementService(UserRepository userRepository,
                                 RoleRepository roleRepository,
                                 PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** اختصاص نقش به کاربر با ID نقش */
    public void assignRoleToUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.withResource("User", userId));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> ResourceNotFoundException.withResource("Role", roleId));

        user.getRoles().add(role);
        userRepository.save(user);
    }

    /** حذف نقش از کاربر */
    public void removeRoleFromUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.withResource("User", userId));

        user.getRoles().removeIf(role -> role.getId().equals(roleId));
        userRepository.save(user);
    }

    /** جایگزین کردن تمام نقش‌های کاربر */
    public void updateUserRoles(Long userId, Set<Long> roleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.withResource("User", userId));

        Set<Role> newRoles = roleIds.stream()
                .map(id -> roleRepository.findById(id)
                        .orElseThrow(() -> ResourceNotFoundException.withResource("Role", id)))
                .collect(Collectors.toSet());

        user.getRoles().clear();
        user.getRoles().addAll(newRoles);
        userRepository.save(user);
    }

    /** ثبت کاربر جدید */
    public User registerUser(String username, String email, String password,
                             String firstName, String lastName) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username is already taken!");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already in use!");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);

        // نقش پیش‌فرض
        Role techRole = roleRepository.findByName("TECHNICIAN")
                .orElseThrow(() -> new RuntimeException("Default role TECHNICIAN not found!"));
        user.getRoles().add(techRole);

        User savedUser = userRepository.save(user);

        // بازگرداندن User با اطلاعات کامل
        return userRepository.findByIdWithRolesAndPermissions(savedUser.getId())
                .orElse(savedUser);
    }

    /** حذف نرم کاربر */
    public void softDeleteUser(Long userId, String deletedBy) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.withResource("User", userId));
        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        user.setDeletedBy(deletedBy);
        userRepository.save(user);
    }

    /** بازگردانی کاربر */
    public void restoreUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.withResource("User", userId));
        user.setDeleted(false);
        user.setDeletedAt(null);
        user.setDeletedBy(null);
        userRepository.save(user);
    }

    /** تغییر وضعیت فعال/غیرفعال کاربر */
    public void toggleUserStatus(Long userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.withResource("User", userId));
        user.setEnabled(enabled);
        userRepository.save(user);
    }

    /** جستجوی کاربران */
    public List<UserResponse> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllUsers();
        }

        return userRepository.searchUsers("%" + keyword.trim() + "%")
                .stream()
                .map(UserResponse::new) // استفاده از constructor جدید
                .collect(Collectors.toList());
    }

    /** دریافت کاربر بر اساس ID */
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> ResourceNotFoundException.withResource("User", userId));

        return new UserResponse(user); // استفاده از constructor جدید
    }

    /** دریافت تمام کاربران */
    public List<UserResponse> getAllUsers() {
        return userRepository.findByDeletedFalse().stream()
                .map(UserResponse::new) // استفاده از constructor جدید
                .collect(Collectors.toList());
    }

    /** دریافت کاربران فعال */
    public List<UserResponse> getActiveUsers() {
        return userRepository.findByEnabledTrueAndDeletedFalse().stream()
                .map(UserResponse::new) // استفاده از constructor جدید
                .collect(Collectors.toList());
    }

    public UserResponse convertToResponse(User user) {
        // لود کردن User با roles و permissions
        User loadedUser = userRepository.findByIdWithRolesAndPermissions(user.getId())
                .orElse(user); // اگر متد وجود نداشت، از user اصلی استفاده کن

        return new UserResponse(loadedUser);
    }
}