package com.vveed.permissions.services.background;

import com.vveed.permissions.domain.Error;
import com.vveed.permissions.domain.Vacuum;
import com.vveed.permissions.domain.enums.VacuumStatus;
import com.vveed.permissions.repositories.ErrorsRepository;
import com.vveed.permissions.repositories.VacuumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class BackgroundTaskService {

    private final VacuumRepository vacuumRepository;
    private final ErrorsRepository errorsRepository;

    @Autowired
    public BackgroundTaskService(VacuumRepository vacuumRepository, ErrorsRepository errorsRepository){
        this.vacuumRepository = vacuumRepository;
        this.errorsRepository = errorsRepository;
    }

    @Async("backgroundTaskExecutor")
    public CompletableFuture<Void> doJob(Long vacuum_id, Long user_id, VacuumStatus actionToPerform){
        try{
            System.out.println("VACUUM state change to -> " + actionToPerform.toString());
            switch(actionToPerform){
                case RUNNING:
                    startCleaner(vacuum_id, user_id);
                    System.out.println("STARTED VACUUM");
                    break;
                case STOPPED:
                    stopCleaner(vacuum_id, user_id);
                    System.out.println("STOPPED VACUUM");
                    break;
                case DISCHARGING:
                    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                    executorService.schedule(doDischarge(vacuum_id, user_id), 15, TimeUnit.SECONDS);
                    break;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(null);
    }

    public void startCleaner(Long vacuum_id, Long user_id) throws InterruptedException {
        System.out.println("STARTING CLEANER....");
        Vacuum vacuum = this.vacuumRepository.findById(vacuum_id).get();

        try{
            if(vacuum.getStatus() != VacuumStatus.STOPPED) {
                System.out.println("NIJE STOPPED " + vacuum.getName() + " " + vacuum.getStatus());
                return;
            }

            Thread.sleep(15000);

            VacuumStatus vacuumStatus = VacuumStatus.RUNNING;

            this.vacuumRepository.update(vacuumStatus, user_id, vacuum.getId());
        }catch (ObjectOptimisticLockingFailureException exception){
            createError(vacuum_id, new Date().getTime(), "START", "Vacuum failed to start");
            this.startCleaner(vacuum_id, user_id);
        }
    }

    private void createError(Long vacuum_id, Long time, String operation, String message){
        Error error = new Error();
        error.setVaccum_id(vacuum_id);
        error.setTime(time);
        error.setOperation(operation);
        error.setMessage(message);

        this.errorsRepository.save(error);
    }

    public void stopCleaner(Long vacuum_id, Long user_id) throws InterruptedException {
        try {
            System.out.println("STOPPING CLEANER....");

            Vacuum vacuum = this.vacuumRepository.findById(vacuum_id).get();

            if(vacuum.getStatus() != VacuumStatus.RUNNING){
                System.out.println("NIJE RUNNING " + vacuum.getName() + " " + vacuum.getStatus());
                return;
            }

            Thread.sleep(20000);

            VacuumStatus vacuumStatus = VacuumStatus.STOPPED;
            this.vacuumRepository.update(vacuumStatus, user_id, vacuum_id);

            if(vacuum.getDischargeFlag() == 3){
                doJob(vacuum_id, user_id, VacuumStatus.DISCHARGING);
                this.vacuumRepository.resetDischargeCount(vacuum_id);
            }

            this.vacuumRepository.incrementDischargeCount(vacuum_id);
        }catch (ObjectOptimisticLockingFailureException exception){
            createError(vacuum_id, new Date().getTime(), "STOP", "Vacuum failed to stop");
            this.stopCleaner(vacuum_id, user_id);
        }
    }

    public Runnable doDischarge(Long vacuum_id, Long user_id){
        return () -> {
            try{
                Vacuum vacuum = this.vacuumRepository.findById(vacuum_id).get();

                if(vacuum.getStatus() != VacuumStatus.STOPPED){
                    System.out.println("NIJE STOPPED " + vacuum.getStatus());
                    return;
                }

                VacuumStatus vacuumStatus = VacuumStatus.DISCHARGING;
                this.vacuumRepository.update(vacuumStatus, user_id, vacuum_id);

                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                executorService.schedule(doSecondPart(vacuum_id, user_id), 15, TimeUnit.SECONDS);
            }catch (ObjectOptimisticLockingFailureException exception){
                createError(vacuum_id, new Date().getTime(), "DISCHARGE", "Vacuum failed to DISCHARGE");
                this.doDischarge(vacuum_id, user_id);
            }
        };
    }

    @Transactional
    public Runnable doSecondPart(Long vacuum_id, Long user_id){
        return () -> {
            try {
                Vacuum vacuum = this.vacuumRepository.findById(vacuum_id).get();

                if(vacuum.getStatus() != VacuumStatus.DISCHARGING){
                    System.out.println("NIJE DISCHARGING " + vacuum.getStatus());
                    return;
                }
                VacuumStatus vacuumStatus = VacuumStatus.STOPPED;
                this.vacuumRepository.update(vacuumStatus, user_id, vacuum_id);
//                vacuumRepository.update(VacuumStatus.STOPPED, user_id, vacuum_id);
            }catch (ObjectOptimisticLockingFailureException exception){
                createError(vacuum_id, new Date().getTime(), "STOP", "Vacuum failed to stop");
                this.doSecondPart(vacuum_id, user_id);
            }
        };
    }
}
