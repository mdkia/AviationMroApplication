package com.aviation.mro.config;


import com.aviation.mro.modules.auth.repository.UserRepository;
import com.aviation.mro.shared.security.JwtAuthenticationFilter;
import com.aviation.mro.shared.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private static final String[] PUBLIC_URLS = {
            // Swagger
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",

            // Health checks
            "/api/health",
            "/api/info",

            // Auth
            "/api/auth/**"
    };

    public SecurityConfig(JwtTokenProvider tokenProvider, UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(PUBLIC_URLS).permitAll()

                        // مدیریت کاربران - فقط ادمین
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // مدیریت قطعات
                        .requestMatchers(HttpMethod.GET, "/api/parts/**").hasAnyRole(
                                "ADMIN", "TECHNICIAN", "INSPECTOR", "WAREHOUSE_MANAGER",
                                "SALES_MANAGER", "ACCOUNTANT", "READ_ONLY")
                        .requestMatchers(HttpMethod.POST, "/api/parts/**").hasAnyRole(
                                "ADMIN", "TECHNICIAN")
                        .requestMatchers(HttpMethod.PUT, "/api/parts/**").hasAnyRole(
                                "ADMIN", "TECHNICIAN", "INSPECTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/parts/**").hasRole("ADMIN")

                        // Warehouse (بعداً کامل می‌شود)
                        .requestMatchers("/api/warehouse/**").hasAnyRole(
                                "ADMIN", "WAREHOUSE_MANAGER", "TECHNICIAN", "INSPECTOR")

                        // Sales (بعداً کامل می‌شود)
                        .requestMatchers("/api/sales/**").hasAnyRole("ADMIN", "SALES_MANAGER")

                        // Accounting (بعداً کامل می‌شود)
                        .requestMatchers("/api/accounting/**").hasAnyRole("ADMIN", "ACCOUNTANT")

                        .anyRequest().authenticated()
                )
               /* .addFilterBefore(new JwtAuthenticationFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter.class)*/
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // پاس دادن userRepository به فیلتر
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider, userRepository),
                        UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}