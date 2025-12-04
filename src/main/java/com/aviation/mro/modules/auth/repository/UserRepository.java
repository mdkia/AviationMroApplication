package com.aviation.mro.modules.auth.repository;

import com.aviation.mro.modules.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // متدهای موجود
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByIdAndDeletedFalse(Long id);

    // اضافه کردن این متدها
    Optional<User> findByUsernameAndDeletedFalse(String username); // این متد را اضافه کنید

    // متدهای fetch join
    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.roles r " +
            "LEFT JOIN FETCH r.permissions p " +
            "WHERE u.id = :userId")
    Optional<User> findByIdWithRolesAndPermissions(@Param("userId") Long userId);

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.roles r " +
            "LEFT JOIN FETCH r.permissions p " +
            "WHERE u.username = :username")
    Optional<User> findByUsernameWithRolesAndPermissions(@Param("username") String username);

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.roles r " +
            "LEFT JOIN FETCH r.permissions p " +
            "WHERE u.username = :username AND u.deleted = false")
    Optional<User> findByUsernameWithRolesAndPermissionsAndNotDeleted(@Param("username") String username);

    // متدهای exists
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByUsernameAndDeletedFalse(String username);
    Boolean existsByEmailAndDeletedFalse(String email);

    // متدهای find با conditions
    List<User> findByDeletedFalse();
    List<User> findByEnabledTrueAndDeletedFalse();

    // متدهای جستجو
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.email LIKE %:keyword% " +
            "OR u.firstName LIKE %:keyword% OR u.lastName LIKE %:keyword%")
    List<User> searchUsers(@Param("keyword") String keyword);

    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.email LIKE %:keyword% " +
            "OR u.firstName LIKE %:keyword% OR u.lastName LIKE %:keyword% AND u.deleted = false")
    List<User> searchActiveUsers(@Param("keyword") String keyword);

    // متدهای کمکی
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.deleted = false")
    List<User> findByRoleNameAndNotDeleted(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u WHERE u.deleted = false AND u.enabled = true")
    List<User> findActiveUsers();
}