package com.vveed.permissions.services;

import com.vveed.permissions.domain.Vacuum;
import com.vveed.permissions.domain.VacuumJob;
import com.vveed.permissions.domain.enums.VacuumStatus;
import com.vveed.permissions.repositories.VacuumJobRepository;
import com.vveed.permissions.repositories.VacuumRepository;
import com.vveed.permissions.services.background.BackgroundTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class VacuumJobsService implements IService<VacuumJob, Long>{

    private final VacuumJobRepository vacuumJobRepository;
    private final VacuumRepository vacuumRepository;
    private final VacuumService vacuumService;

    private Set<Long> scheduledJobs = new HashSet<>();

    @Autowired
    private BackgroundTaskService backgroundTaskService;

    @Autowired
    private TaskScheduler taskScheduler;

    public VacuumJobsService(VacuumJobRepository vacuumJobRepository,
                             VacuumRepository vacuumRepository,
                             VacuumService vacuumService){
        this.vacuumJobRepository = vacuumJobRepository;
        this.vacuumRepository = vacuumRepository;
        this.vacuumService = vacuumService;
    }

    @Override
    public List<VacuumJob> findAll() {
        return this.vacuumJobRepository.findAll();
    }

    @Override
    public Optional<VacuumJob> findById(Long id) {
        return this.vacuumJobRepository.findById(id);
    }

    @Override
    public VacuumJob save(VacuumJob vacuumJob) {
        vacuumJob.setIs_active(true);
        return this.vacuumJobRepository.save(vacuumJob);
    }

    @Override
    public void deleteById(Long id) {
        this.vacuumJobRepository.deleteById(id);
    }

    public void switchJob(VacuumJob vacuumJob){
        try{
            this.vacuumJobRepository.switchJob(!vacuumJob.isIs_active(), vacuumJob.getVacuum_id());
        }catch(ObjectOptimisticLockingFailureException exception){
            switchJob(vacuumJob);
        }
    }

    @Scheduled(fixedDelay = 60000)
    public void scheduleJobs() throws InterruptedException {
        List<VacuumJob> jobs = this.vacuumJobRepository.findAll();

        System.out.println("JOBS " + jobs.size());

        for(VacuumJob job : jobs){
            if(this.scheduledJobs.contains(job.getId()) || !job.isIs_active())
                continue;

            Vacuum vacuum = this.vacuumRepository.findById(job.getVacuum_id()).get();

            this.taskScheduler.schedule(() -> this.vacuumService.performAction(job.getVacuum_id(), vacuum.getAdded_by(), job.getAction()), new CronTrigger(String.valueOf(job.getTime()) + " * * * * ?"));
            this.scheduledJobs.add(job.getId());
        }
    }


    private String getCronExpression(Long timestamp){
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());

        int minute = dateTime.getMinute();
        int hour = dateTime.getHour();
        int dayOfMonth = dateTime.getDayOfMonth();
        int month = dateTime.getMonthValue();
        int dayOfWeek = dateTime.getDayOfWeek().getValue();

        System.out.println(String.format("%d %d %d %d %d ?", minute, hour, dayOfMonth, month, dayOfWeek));

        return String.format("%d %d %d %d %d ?", minute, hour, dayOfMonth, month, dayOfWeek);
    }
}
