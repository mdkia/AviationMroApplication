package com.aviation.mro.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class DeleteUserRequest {

    @NotBlank(message = "Deleted by username cannot be blank")
    private String deletedBy;

    // Constructors
    public DeleteUserRequest() {}

    public DeleteUserRequest(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    // Getters and Setters
    public String getDeletedBy() { return deletedBy; }
    public void setDeletedBy(String deletedBy) { this.deletedBy = deletedBy; }
}
