package com.vveed.permissions.controllers;

import com.vveed.permissions.domain.Error;
import com.vveed.permissions.domain.User;
import com.vveed.permissions.services.ErrorService;
import com.vveed.permissions.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/errors")
public class ErrorsController {

    private ErrorService errorService;
    private UserService userService;

    public ErrorsController(ErrorService errorService,
                            UserService userService){
        this.errorService = errorService;
        this.userService = userService;
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Error> getErrors(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = this.userService.findByEmail(userDetails.getUsername());

        return this.errorService.findAll(user.getId());
    }
}
