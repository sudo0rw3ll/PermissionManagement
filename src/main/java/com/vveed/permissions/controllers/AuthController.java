package com.vveed.permissions.controllers;

import com.vveed.permissions.domain.Permission;
import com.vveed.permissions.domain.User;
import com.vveed.permissions.requests.LoginRequest;
import com.vveed.permissions.responses.LoginResponse;
import com.vveed.permissions.services.UserService;
import com.vveed.permissions.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil){
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(401).build();
        }

        User user = this.userService.findByEmail(loginRequest.getEmail());

        String roles = "[";

        for(Permission permission : user.getPermissions()){
            roles += permission.getPermission() + ", ";
        }

        roles = roles.substring(0, roles.lastIndexOf(','));
        roles += "]";

        return ResponseEntity.ok(new LoginResponse(jwtUtil.generateToken(loginRequest.getEmail(), roles)));
    }
}
