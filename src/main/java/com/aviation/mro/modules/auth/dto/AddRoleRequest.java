package com.aviation.mro.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AddRoleRequest {

    @NotBlank(message = "Role cannot be blank")
    @Pattern(regexp = "^[A-Z_]+$", message = "Role must contain only uppercase letters and underscores")
    private String role;

    // Constructors
    public AddRoleRequest() {}

    public AddRoleRequest(String role) {
        this.role = role;
    }

    // Getters and Setters
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
