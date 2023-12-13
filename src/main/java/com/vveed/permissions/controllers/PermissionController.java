package com.vveed.permissions.controllers;

import com.vveed.permissions.domain.Permission;
import com.vveed.permissions.domain.User;
import com.vveed.permissions.services.PermissionService;
import com.vveed.permissions.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {
    private final PermissionService permissionService;
    private final UserService userService;

    public PermissionController(PermissionService permissionService, UserService userService){
        this.permissionService = permissionService;
        this.userService = userService;
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Permission> getAllPermissions(){return permissionService.findAll();}


    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPermissionById(@PathVariable("id") Long id){
        Optional<Permission> optionalPermission = permissionService.findById(id);
        if(optionalPermission.isPresent()){
            return ResponseEntity.ok(optionalPermission.get());
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Permission createPermission(@RequestBody Permission permission){
        return permissionService.save(permission);
    }

    @PostMapping(value = "/grant/{permissionId}/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> grantPermissionToUser(@PathVariable("permissionId") Long permissionId, @PathVariable("userId") Long userId){
        Permission permission = permissionService.findById(permissionId).get();

        permission.addUser(userService.findById(userId).get());
        permissionService.save(permission);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/disable/{permissionId}/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> disablePermissionToUser(@PathVariable("permissionId") Long permissionId, @PathVariable("userId") Long userId){
        Permission permission = permissionService.findById(permissionId).get();
        User user = userService.findById(userId).get();

        permission.removeUser(user);
        permissionService.save(permission);

        return ResponseEntity.ok().build();
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Permission updatePermission(@RequestBody Permission permission){
        return permissionService.save(permission);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deletePermission(@PathVariable("id") Long id){
        this.permissionService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
