package com.vveed.permissions.bootstrap;

import com.vveed.permissions.domain.Permission;
import com.vveed.permissions.domain.User;
import com.vveed.permissions.repositories.PermissionRepository;
import com.vveed.permissions.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
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

        String permissions[] = new String[]{
                "can_read_users", "can_create_users",
                "can_update_users", "can_delete_users",
                "admin", "can_search_vacuum",
                "can_start_vacuum", "can_stop_vacuum",
                "can_discharge_vacuum", "can_add_vacuum",
                "can_remove_vacuums"};

        for(int i=0; i < permissions.length; i++){
            Permission permission = new Permission();
            permission.setPermission(permissions[i]);
            this.permissionRepository.save(permission);
        }

        User user1 = new User();
        user1.setPassword(this.passwordEncoder.encode("proba123"));
        user1.setEmail("test123@gmail.com");
        user1.setFirstName("Testni");
        user1.setLastName("Akaunt");

        Permission permissionFetch = this.permissionRepository.findById(1L).get();
        Permission permission2Fetch = this.permissionRepository.findById(2L).get();
        Permission permission3Fetch = this.permissionRepository.findById(10L).get();

        Set<Permission> permissionSet = new HashSet<>();
        permissionSet.add(permissionFetch);
        permissionSet.add(permission2Fetch);
        permissionSet.add(permission3Fetch);
        user1.setPermissions(permissionSet);

        this.userRepository.save(user1);

        System.out.println("Loaded!");
    }
}
