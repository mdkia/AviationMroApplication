package com.aviation.mro.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private String username;
    private String email;
    private Set<String> roles;

    private Set<String> permissions; // اگر نیاز دارید

    public AuthResponse(String accessToken, String username, String email, Set<String> roles) {
        this.accessToken = accessToken;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
