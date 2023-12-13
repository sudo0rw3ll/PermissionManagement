package com.vveed.permissions.repositories;

import com.vveed.permissions.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
