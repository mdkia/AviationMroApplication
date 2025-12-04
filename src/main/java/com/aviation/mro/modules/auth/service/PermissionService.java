package com.aviation.mro.modules.auth.service;

import com.aviation.mro.modules.auth.model.Permission;
import com.aviation.mro.modules.auth.repository.PermissionRepository;
import com.aviation.mro.shared.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission createPermission(String name, String module, String description) {
        if (permissionRepository.existsByName(name)) {
            throw new IllegalArgumentException("Permission already exists: " + name);
        }

        Permission permission = new Permission();
        permission.setName(name.toUpperCase());
        permission.setModule(module);
        permission.setDescription(description);
        return permissionRepository.save(permission);
    }

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    public Permission getPermissionById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.withResource("Permission", id));
    }
}