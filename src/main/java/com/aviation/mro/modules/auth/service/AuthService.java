package com.aviation.mro.modules.auth.service;

import com.aviation.mro.modules.auth.dto.AuthResponse;
import com.aviation.mro.modules.auth.dto.LoginRequest;
import com.aviation.mro.modules.auth.model.Permission;
import com.aviation.mro.modules.auth.model.Role;
import com.aviation.mro.modules.auth.model.User;
import com.aviation.mro.modules.auth.repository.RoleRepository;
import com.aviation.mro.modules.auth.repository.UserRepository;
import com.aviation.mro.shared.common.ApiResponse;
import com.aviation.mro.shared.exceptions.UserAlreadyExistsException;
import com.aviation.mro.shared.exceptions.UserNotFoundException;
import com.aviation.mro.shared.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public AuthResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            User user = userRepository.findByUsernameAndDeletedFalse(loginRequest.getUsername())
                    .orElseThrow(() ->
                            new UsernameNotFoundException("User not found: " + loginRequest.getUsername()));

            // جمع‌آوری نقش‌ها
            Set<String> roleNames = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());

            // جمع‌آوری دسترسی‌ها
            Set<String> permissionNames = user.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .map(Permission::getName)
                    .collect(Collectors.toSet());

            // برگرداندن DTO
            return new AuthResponse(jwt, user.getUsername(), user.getEmail(), roleNames);

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage().toString());
        }
    }
}