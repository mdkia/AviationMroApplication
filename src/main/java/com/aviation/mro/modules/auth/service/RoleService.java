package com.aviation.mro.modules.auth.service;

import com.aviation.mro.modules.auth.model.Permission;
import com.aviation.mro.modules.auth.model.Role;
import com.aviation.mro.modules.auth.repository.PermissionRepository;
import com.aviation.mro.modules.auth.repository.RoleRepository;
import com.aviation.mro.shared.exceptions.InvalidRoleException;
import com.aviation.mro.shared.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public Role createRole(String name, String displayName, String description) {
        if (roleRepository.existsByName(name)) {
            throw InvalidRoleException.withRole(name);
        }

        Role role = new Role();
        role.setName(name.toUpperCase());
        role.setDisplayName(displayName);
        role.setDescription(description);
        return roleRepository.save(role);
    }

    public Role updateRolePermissions(Long roleId, Set<String> permissionNames) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> ResourceNotFoundException.withResource("Role", roleId));

        Set<Permission> permissions = permissionNames.stream()
                .map(name -> permissionRepository.findByName(name)
                        .orElseThrow(() -> ResourceNotFoundException.withResource("Permission", name)))
                .collect(Collectors.toSet());

        role.getPermissions().clear();
        role.getPermissions().addAll(permissions);
        return roleRepository.save(role);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.withResource("Role", id));
    }

    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.withResource("Role", id));
        if (role.isSystem()) {
            throw new IllegalStateException("Cannot delete system role: " + role.getName());
        }
        roleRepository.delete(role);
    }
}