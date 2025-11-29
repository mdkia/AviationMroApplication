package com.aviation.mro.shared.security;

import com.aviation.mro.modules.auth.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        // اگر principal از نوع مدل User شما هست
        if (principal instanceof User) {
            return ((User) principal).getUsername();
        }

        // اگر principal یک String هست (username)
        if (principal instanceof String) {
            return (String) principal;
        }

        // اگر principal یک UserDetails استاندارد Spring هست
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            return ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        }

        // در غیر این صورت از authentication.getName() استفاده کن
        return authentication.getName();
    }

    public static String getCurrentUsername(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        // اگر principal از نوع مدل User شما هست
        if (principal instanceof User) {
            return ((User) principal).getUsername();
        }

        // اگر principal یک String هست (username)
        if (principal instanceof String) {
            return (String) principal;
        }

        // اگر principal یک UserDetails استاندارد Spring هست
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            return ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        }

        // در غیر این صورت از authentication.getName() استفاده کن
        return authentication.getName();
    }

    // متد اضافی برای گرفتن خود User object
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            return (User) principal;
        }

        return null;
    }

    public static User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            return (User) principal;
        }

        return null;
    }
}