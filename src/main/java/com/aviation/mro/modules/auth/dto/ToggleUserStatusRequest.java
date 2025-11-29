package com.aviation.mro.modules.auth.dto;

public class ToggleUserStatusRequest {
    private boolean enabled;

    // Constructors
    public ToggleUserStatusRequest() {}

    public ToggleUserStatusRequest(boolean enabled) {
        this.enabled = enabled;
    }

    // Getters and Setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
