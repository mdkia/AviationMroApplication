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

    // لیست URLهای عمومی
    private static final String[] PUBLIC_URLS = {
            // Swagger UI
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**",

            // Health check
            "/actuator/health",
            "/api/health",
            "/api/info",

            // Authentication
            "/api/auth/**",

            // Public APIs (در صورت نیاز)
            "/api/public/**"
    };

    public SecurityConfig(JwtTokenProvider tokenProvider, UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configure(http)) // اگر CORS نیاز دارید
                .authorizeHttpRequests(authz -> authz
                        // ============ عمومی ============
                        .requestMatchers(PUBLIC_URLS).permitAll()

                        // مدیریت نقش‌ها و دسترسی‌ها
                        .requestMatchers("/api/admin/roles/**").hasAuthority("MANAGE_ROLES")
                        .requestMatchers("/api/admin/users/**").hasAuthority("MANAGE_USERS")
                        .requestMatchers("/api/admin/permissions/**").hasAuthority("MANAGE_PERMISSIONS")

                        // یا همه admin endpointها با یک permission
                        .requestMatchers("/api/admin/**").hasAuthority("MANAGE_SYSTEM")
                        // ============ مدیریت سیستم ============
                        .requestMatchers("/api/admin/**").hasAuthority("MANAGE_SYSTEM")

                        // ============ ماژول قطعات (Parts) ============
                        .requestMatchers(HttpMethod.GET, "/api/parts/**").hasAnyAuthority(
                                "VIEW_PARTS", "CREATE_PARTS", "EDIT_PARTS", "DELETE_PARTS",
                                "APPROVE_PARTS", "REJECT_PARTS", "MANAGE_SYSTEM")

                        .requestMatchers(HttpMethod.POST, "/api/parts/**").hasAnyAuthority(
                                "CREATE_PARTS", "MANAGE_SYSTEM")

                        .requestMatchers(HttpMethod.PUT, "/api/parts/**").hasAnyAuthority(
                                "EDIT_PARTS", "APPROVE_PARTS", "REJECT_PARTS", "MANAGE_SYSTEM")

                        .requestMatchers(HttpMethod.DELETE, "/api/parts/**").hasAnyAuthority(
                                "DELETE_PARTS", "MANAGE_SYSTEM")

                        .requestMatchers("/api/parts/*/approve").hasAnyAuthority(
                                "APPROVE_PARTS", "MANAGE_SYSTEM")

                        .requestMatchers("/api/parts/*/reject").hasAnyAuthority(
                                "REJECT_PARTS", "MANAGE_SYSTEM")

                        // ============ ماژول انبار (Warehouse/Inventory) ============
                        .requestMatchers(HttpMethod.GET, "/api/warehouse/**").hasAnyAuthority(
                                "VIEW_INVENTORY", "MANAGE_INVENTORY", "UPDATE_STOCK",
                                "MANAGE_WAREHOUSES", "MANAGE_SYSTEM")

                        .requestMatchers(HttpMethod.POST, "/api/warehouse/**").hasAnyAuthority(
                                "MANAGE_INVENTORY", "UPDATE_STOCK", "PROCUREMENT_REQUEST",
                                "MANAGE_WAREHOUSES", "MANAGE_SYSTEM")

                        .requestMatchers(HttpMethod.PUT, "/api/warehouse/**").hasAnyAuthority(
                                "MANAGE_INVENTORY", "UPDATE_STOCK", "MANAGE_WAREHOUSES",
                                "MANAGE_SYSTEM")

                        .requestMatchers("/api/warehouse/*/approve").hasAnyAuthority(
                                "APPROVE_PROCUREMENT", "MANAGE_SYSTEM")

                        // ============ ماژول فروش (Sales) ============
                        .requestMatchers(HttpMethod.GET, "/api/sales/**").hasAnyAuthority(
                                "VIEW_SALES", "MANAGE_SALES", "VIEW_CUSTOMERS",
                                "MANAGE_CUSTOMERS", "MANAGE_SYSTEM")

                        .requestMatchers(HttpMethod.POST, "/api/sales/**").hasAnyAuthority(
                                "MANAGE_SALES", "CREATE_ORDER", "CREATE_QUOTATION",
                                "CREATE_INVOICE", "MANAGE_CUSTOMERS", "MANAGE_SYSTEM")

                        .requestMatchers("/api/sales/*/approve").hasAnyAuthority(
                                "APPROVE_QUOTATION", "MANAGE_SYSTEM")

                        // ============ ماژول حسابداری (Accounting) ============
                        .requestMatchers(HttpMethod.GET, "/api/accounting/**").hasAnyAuthority(
                                "VIEW_ACCOUNTING", "MANAGE_ACCOUNTING", "VIEW_ACCOUNTS",
                                "VIEW_FINANCIAL_REPORTS", "MANAGE_SYSTEM")

                        .requestMatchers(HttpMethod.POST, "/api/accounting/**").hasAnyAuthority(
                                "MANAGE_ACCOUNTING", "MANAGE_ACCOUNTS", "CREATE_TRANSACTION",
                                "MANAGE_SYSTEM")

                        .requestMatchers("/api/accounting/*/approve").hasAnyAuthority(
                                "APPROVE_TRANSACTION", "MANAGE_SYSTEM")

                        // ============ ماژول کیفیت (Quality) ============
                        .requestMatchers(HttpMethod.GET, "/api/quality/**").hasAnyAuthority(
                                "VIEW_QUALITY", "MANAGE_QUALITY", "VIEW_NON_CONFORMANCE",
                                "MANAGE_SYSTEM")

                        .requestMatchers(HttpMethod.POST, "/api/quality/**").hasAnyAuthority(
                                "MANAGE_QUALITY", "CREATE_INSPECTION", "MANAGE_QUALITY_PLANS",
                                "MANAGE_NON_CONFORMANCE", "MANAGE_SYSTEM")

                        .requestMatchers("/api/quality/*/approve").hasAnyAuthority(
                                "APPROVE_INSPECTION", "MANAGE_SYSTEM")

                        .requestMatchers("/api/quality/*/reject").hasAnyAuthority(
                                "REJECT_INSPECTION", "MANAGE_SYSTEM")

                        // ============ ماژول تعمیرات (Repair/Maintenance) ============
                        .requestMatchers(HttpMethod.GET, "/api/repair/**").hasAnyAuthority(
                                "VIEW_REPAIR", "MANAGE_REPAIR", "VIEW_REPAIR_HISTORY",
                                "MANAGE_SYSTEM")

                        .requestMatchers(HttpMethod.POST, "/api/repair/**").hasAnyAuthority(
                                "MANAGE_REPAIR", "CREATE_WORK_ORDER", "ASSIGN_WORK_ORDER",
                                "MANAGE_REPAIR_SCHEDULE", "MANAGE_SYSTEM")

                        .requestMatchers("/api/repair/*/complete").hasAnyAuthority(
                                "COMPLETE_WORK_ORDER", "MANAGE_SYSTEM")

                        // ============ ماژول گزارشات (Reports) ============
                        .requestMatchers(HttpMethod.GET, "/api/reports/**").hasAnyAuthority(
                                "VIEW_REPORTS", "EXPORT_REPORTS", "VIEW_DASHBOARD",
                                "CUSTOM_REPORT", "MANAGE_SYSTEM")

                        .requestMatchers(HttpMethod.POST, "/api/reports/**").hasAnyAuthority(
                                "EXPORT_REPORTS", "CUSTOM_REPORT", "MANAGE_SYSTEM")

                        // ============ ماژول اطلاع‌رسانی (Notifications) ============
                        .requestMatchers(HttpMethod.GET, "/api/notifications/**").hasAnyAuthority(
                                "VIEW_NOTIFICATIONS", "MANAGE_NOTIFICATIONS", "MANAGE_SYSTEM")

                        .requestMatchers(HttpMethod.POST, "/api/notifications/**").hasAnyAuthority(
                                "MANAGE_NOTIFICATIONS", "SEND_NOTIFICATIONS", "MANAGE_SYSTEM")

                        // ============ ماژول تنظیمات (Settings) ============
                        .requestMatchers(HttpMethod.GET, "/api/settings/**").hasAnyAuthority(
                                "VIEW_SETTINGS", "MANAGE_SETTINGS", "MANAGE_COMPANY_INFO",
                                "MANAGE_TAX_SETTINGS", "MANAGE_SYSTEM")

                        .requestMatchers(HttpMethod.PUT, "/api/settings/**").hasAnyAuthority(
                                "MANAGE_SETTINGS", "MANAGE_COMPANY_INFO", "MANAGE_TAX_SETTINGS",
                                "MANAGE_SYSTEM")

                        // ============ سایر endpointها ============
                        .requestMatchers("/api/upload/**").hasAnyAuthority(
                                "CREATE_PARTS", "IMPORT_PARTS", "MANAGE_SYSTEM")

                        .requestMatchers("/api/export/**").hasAnyAuthority(
                                "EXPORT_PARTS", "EXPORT_REPORTS", "EXPORT_FINANCIAL_REPORTS",
                                "MANAGE_SYSTEM")

                        // ============ مدیریت فایل‌ها ============
                        .requestMatchers(HttpMethod.GET, "/api/files/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/files/**").hasAnyAuthority(
                                "CREATE_PARTS", "MANAGE_SYSTEM")

                        // ============ هر درخواست دیگر نیاز به احراز هویت دارد ============
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
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