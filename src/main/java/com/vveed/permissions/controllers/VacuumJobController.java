package com.vveed.permissions.controllers;

import com.vveed.permissions.domain.VacuumJob;
import com.vveed.permissions.repositories.VacuumJobRepository;
import com.vveed.permissions.repositories.VacuumRepository;
import com.vveed.permissions.services.VacuumJobsService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/jobs")
public class VacuumJobController {

    private final VacuumJobRepository vacuumJobRepository;
    private final VacuumRepository vacuumRepository;
    private final VacuumJobsService vacuumJobsService;

    public VacuumJobController(VacuumJobRepository vacuumJobRepository,
                               VacuumRepository vacuumRepository,
                               VacuumJobsService vacuumJobsService){
        this.vacuumJobRepository = vacuumJobRepository;
        this.vacuumRepository = vacuumRepository;
        this.vacuumJobsService = vacuumJobsService;
    }

    @GetMapping(value="/", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VacuumJob> getVacuumJobs() {
        return this.vacuumJobsService.findAll();
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
