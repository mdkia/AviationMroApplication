package com.aviation.mro.modules.auth.dto;

public record RegisterRequest(
        String username,
        String email,
        String password,
        String firstName,
        String lastName
) {}