package com.vveed.permissions.controllers;

import com.vveed.permissions.domain.Permission;
import com.vveed.permissions.domain.User;
import com.vveed.permissions.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('can_read_users')")
    public List<User> getAllUsers(){
        return this.userService.findAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('can_read_users')")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id){
        Optional<User> optionalUser = userService.findById(id);
        if(optionalUser.isPresent()){
            return ResponseEntity.ok(optionalUser.get());
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('can_create_users')")
    public User createUser(@RequestBody User user) {
//        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        System.out.println("User Roles: " + userDetails.getAuthorities());
        return userService.save(user);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('can_update_users')")
    public User updateUser(@RequestBody User user){
        return userService.save(user);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('can_delete_users')")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id){
        Optional<User> optionalUser = userService.findById(id);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();

            Object[] permissions = user.getPermissions().toArray();

            for(int i=0;i<permissions.length; i++){
                ((Permission)permissions[i]).removeUser(user);
            }

            userService.deleteById(id);

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }
}
