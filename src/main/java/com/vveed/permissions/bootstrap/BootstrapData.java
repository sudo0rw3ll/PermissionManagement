package com.vveed.permissions.bootstrap;

import com.vveed.permissions.domain.Permission;
import com.vveed.permissions.domain.User;
import com.vveed.permissions.repositories.PermissionRepository;
import com.vveed.permissions.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
public class BootstrapData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public BootstrapData(UserRepository userRepository,
                         PermissionRepository permissionRepository,
                         PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("Loading...");

        Permission permission = new Permission();
        permission.setPermission("can_read_users");
        this.permissionRepository.save(permission);

        Permission permission1 = new Permission();
        permission1.setPermission("can_create_users");
        this.permissionRepository.save(permission1);

        Permission permission2 = new Permission();
        permission2.setPermission("can_update_users");
        this.permissionRepository.save(permission2);

        Permission permission3 = new Permission();
        permission3.setPermission("can_delete_users");
        this.permissionRepository.save(permission3);

        Permission permission4 = new Permission();
        permission4.setPermission("admin");
        this.permissionRepository.save(permission4);

        User user1 = new User();
        user1.setPassword(this.passwordEncoder.encode("proba123"));
        user1.setEmail("test123@gmail.com");
        user1.setFirstName("Testni");
        user1.setLastName("Akaunt");

        Permission permissionFetch = this.permissionRepository.findById(1L).get();
        Permission permission2Fetch = this.permissionRepository.findById(2L).get();

        Set<Permission> permissionSet = new HashSet<>();
        permissionSet.add(permissionFetch);
        permissionSet.add(permission2Fetch);
        user1.setPermissions(permissionSet);

        this.userRepository.save(user1);

        System.out.println("Loaded!");
    }
}
