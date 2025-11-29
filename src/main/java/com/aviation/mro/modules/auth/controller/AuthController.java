package com.aviation.mro.modules.auth.controller;

import com.aviation.mro.modules.auth.dto.AuthResponse;
import com.aviation.mro.modules.auth.dto.LoginRequest;
import com.aviation.mro.modules.auth.service.AuthService;
import com.aviation.mro.shared.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    //todo: return user roles
//    @GetMapping("/me")
//    public ResponseEntity<ApiResponse> getCurrentUser() {
//        return ResponseEntity.ok(ApiResponse.success("Current user endpoint"));
//    }
}