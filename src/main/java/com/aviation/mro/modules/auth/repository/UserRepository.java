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

    // پیدا کردن کاربران فعال
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByIdAndDeletedFalse(Long id);
//    Optional<User> findByUsernameAndDeletedFalse(String username);

    // بررسی وجود کاربر
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByUsernameAndDeletedFalse(String username);
    Boolean existsByEmailAndDeletedFalse(String email);

    // پیدا کردن کاربران حذف نشده
    List<User> findByDeletedFalse();

    // جستجوی کاربران - روش صحیح با دو پارامتر
    List<User> findByUsernameContainingOrEmailContaining(String username, String email);

    // یا با استفاده از @Query (اختیاری)
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.email LIKE %:keyword% OR u.firstName LIKE %:keyword% OR u.lastName LIKE %:keyword%")
    List<User> searchUsers(@Param("keyword") String keyword);

    // پیدا کردن کاربران بر اساس نقش
    @Query("SELECT u FROM User u WHERE :role MEMBER OF u.roles")
    List<User> findByRolesContaining(@Param("role") String role);

    // پیدا کردن کاربران فعال با نقش خاص
    @Query("SELECT u FROM User u WHERE :role MEMBER OF u.roles AND u.deleted = false")
    List<User> findByRolesContainingAndDeletedFalse(@Param("role") String role);

    // اضافه کردن این متد برای فیلتر JWT
    Optional<User> findByUsernameAndDeletedFalse(String username);
}
