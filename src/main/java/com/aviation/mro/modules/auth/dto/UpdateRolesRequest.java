package com.aviation.mro.modules.auth.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public class UpdateRolesRequest {

    @NotNull(message = "Roles cannot be null")
    @Size(min = 1, message = "At least one role must be provided")
    private Set<String> roles;

    // Constructors
    public UpdateRolesRequest() {}

    public UpdateRolesRequest(Set<String> roles) {
        this.roles = roles;
    }

    // Getters and Setters
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}
