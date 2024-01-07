package com.vveed.permissions.controllers;

import com.vveed.permissions.domain.User;
import com.vveed.permissions.domain.Vacuum;
import com.vveed.permissions.domain.enums.VacuumStatus;
import com.vveed.permissions.services.PermissionService;
import com.vveed.permissions.services.UserService;
import com.vveed.permissions.services.VacuumService;
import com.vveed.permissions.services.background.BackgroundTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@CrossOrigin
@RequestMapping("/api/vacuums")
public class VacuumController {

    private final UserService userService;
    private final VacuumService vacuumService;
    private final PermissionService permissionService;

    public VacuumController(UserService userService, VacuumService vacuumService, PermissionService permissionService){
        this.userService = userService;
        this.vacuumService = vacuumService;
        this.permissionService = permissionService;
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Vacuum> getAllVacuums() {return this.vacuumService.findAll();}

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getVacuum(@PathVariable("id") Long id) {
        Optional<Vacuum> optionalVacuum = this.vacuumService.findById(id);
        if(optionalVacuum.isPresent()){
            return ResponseEntity.ok(optionalVacuum.get());
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/search/", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Vacuum> searchVacuums(@RequestParam(required = false) String name, @RequestParam(required = false) List<String> statuses,
                                      @RequestParam(required = false) Long dateFrom, @RequestParam(required = false) Long dateTo){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = this.userService.findByEmail(userDetails.getUsername());

        if (name == null)
            name = "";

//        System.out.println(statuses.toString());

        if (statuses == null)
            statuses = new ArrayList<String>();

        if (dateFrom == null)
            dateFrom = 0L;

        if (dateTo == null)
            dateTo = 0L;

        return this.vacuumService.search(user.getId(), name, statuses, dateFrom, dateTo);
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @PreAuthorize("hasAuthority('can_add_vacuum')")
    public Vacuum createVacuum(@RequestBody Vacuum vacuum){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = this.userService.findByEmail(userDetails.getUsername());

        Vacuum newVacuum = this.vacuumService.save(vacuum);

        user.getCleaners().add(newVacuum);

        return newVacuum;
    }

    @PostMapping(value = "/{id}/changeState")
//    @PreAuthorize("hasAuthority('can_add_vacuum')")
    public ResponseEntity<?> startCleaner(@PathVariable("id") Long id, @RequestParam(required = true) String action){
        Optional<Vacuum> optionalVacuum = this.vacuumService.findById(id);

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = this.userService.findByEmail(userDetails.getUsername());

        if(!optionalVacuum.isPresent())
            return ResponseEntity.notFound().build();

        if(!checkOwnership(optionalVacuum.get())){
            return ResponseEntity.badRequest().build();
        }

        if(action == null){
            return ResponseEntity.badRequest().build();
        }

        switch(action){
            case "START":
                this.vacuumService.performAction(optionalVacuum.get().getId(), user.getId(), VacuumStatus.RUNNING);
                break;
            case "STOP":
                this.vacuumService.performAction(optionalVacuum.get().getId(), user.getId(), VacuumStatus.STOPPED);
                break;
            case "DISCHARGE":
                this.vacuumService.performAction(optionalVacuum.get().getId(), user.getId(), VacuumStatus.DISCHARGING);
                break;
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> disableCleaner(@PathVariable("id") Long id){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = this.userService.findByEmail(userDetails.getUsername());

        Optional<Vacuum> optionalVacuum = this.vacuumService.findById(id);

        if(!optionalVacuum.isPresent())
            return ResponseEntity.notFound().build();

        this.vacuumService.disableCleaner(user.getId(), optionalVacuum.get().getId());
        return ResponseEntity.ok().build();
    }


    private boolean checkOwnership(Vacuum vacuum) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = this.userService.findByEmail(userDetails.getUsername());

        return vacuum.getAdded_by().equals(user.getId());
    }

}
