package com.vveed.permissions.services;

import com.vveed.permissions.domain.Permission;
import com.vveed.permissions.repositories.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionService implements IService<Permission, Long>{

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository){
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<Permission> findAll() {
        return (List<Permission>) this.permissionRepository.findAll();
    }

    @Override
    public Optional<Permission> findById(Long id) {
        return this.permissionRepository.findById(id);
    }

    @Override
    public Permission save(Permission permission) {
        return this.permissionRepository.save(permission);
    }

    @Override
    public void deleteById(Long id) {
        this.permissionRepository.deleteById(id);
    }
}
