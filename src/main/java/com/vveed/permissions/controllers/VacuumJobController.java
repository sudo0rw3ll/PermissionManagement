package com.vveed.permissions.controllers;

import com.vveed.permissions.domain.User;
import com.vveed.permissions.domain.VacuumJob;
import com.vveed.permissions.repositories.UserRepository;
import com.vveed.permissions.repositories.VacuumJobRepository;
import com.vveed.permissions.repositories.VacuumRepository;
import com.vveed.permissions.services.UserService;
import com.vveed.permissions.services.VacuumJobsService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/jobs")
public class VacuumJobController {

    private final VacuumJobRepository vacuumJobRepository;
    private final VacuumRepository vacuumRepository;
    private final UserService userService;
    private final VacuumJobsService vacuumJobsService;

    public VacuumJobController(VacuumJobRepository vacuumJobRepository,
                               VacuumRepository vacuumRepository,
                               VacuumJobsService vacuumJobsService,
                               UserService userService){
        this.vacuumJobRepository = vacuumJobRepository;
        this.vacuumRepository = vacuumRepository;
        this.vacuumJobsService = vacuumJobsService;
        this.userService = userService;
    }

    @GetMapping(value="/", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VacuumJob> getVacuumJobs() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = this.userService.findByEmail(userDetails.getUsername());

        return this.vacuumJobsService.findAll(user.getId());
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public VacuumJob createVacuumJob(@RequestBody VacuumJob vacuumJob){
        System.out.println(vacuumJob);
        return this.vacuumJobsService.save(vacuumJob);
    }

    @GetMapping(value = "/{id}/switch")
    public ResponseEntity<?> switchJobActivity(@RequestParam(value = "id") Long vacuumJobId){
        Optional<VacuumJob> optionalVacuumJob = this.vacuumJobsService.findById(vacuumJobId);

        if(!optionalVacuumJob.isPresent())
            return ResponseEntity.notFound().build();

        this.vacuumJobsService.switchJob(optionalVacuumJob.get());

        return ResponseEntity.ok().build();
    }
}
