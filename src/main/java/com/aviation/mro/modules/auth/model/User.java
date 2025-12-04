package com.aviation.mro.modules.auth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
@Entity
@Table(name = "users")
@Getter
@Setter
@ToString(exclude = {"roles"}) // حذف roles از toString
@EqualsAndHashCode(of = {"id"}) // فقط بر اساس id مقایسه شود
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized @NotBlank @Column(unique = true)
    private String username;

    @Nationalized @NotBlank @Email @Column(unique = true)
    private String email;

    @Nationalized @NotBlank
    private String password;

    @Nationalized @NotBlank
    private String firstName;

    @Nationalized @NotBlank
    private String lastName;

    private boolean enabled = true;
    private boolean deleted = false;
    private LocalDateTime deletedAt;

    @Nationalized
    private String deletedBy;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}