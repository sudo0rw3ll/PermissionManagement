package com.vveed.permissions.services;

import com.vveed.permissions.domain.Vacuum;
import com.vveed.permissions.domain.enums.VacuumStatus;
import com.vveed.permissions.domain.search.VacuumSpecifications;
import com.vveed.permissions.repositories.VacuumRepository;
import com.vveed.permissions.services.background.BackgroundTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VacuumService implements IService<Vacuum, Long>{

    private final VacuumRepository vacuumRepository;

    @Autowired
    private BackgroundTaskService backgroundTaskService;

    public VacuumService(VacuumRepository vacuumRepository){
        this.vacuumRepository = vacuumRepository;
    }

    @Override
    public List<Vacuum> findAll() {
        return this.vacuumRepository.findAll();
    }

    @Override
    public Optional<Vacuum> findById(Long id) {
        return this.vacuumRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        this.vacuumRepository.deleteById(id);
    }

    @Override
    public Vacuum save(Vacuum vacuum) {
//        vacuum.setStatus(VacuumStatus.STOPPED);
        return this.vacuumRepository.save(vacuum);
    }

    public List<Vacuum> search(Long user_id, String name, List<String> statuses, Long dateFrom, Long dateTo){
        Specification<Vacuum> spec = VacuumSpecifications.searchVacuum(user_id, name, statuses, dateFrom, dateTo);
        return vacuumRepository.findAll(spec);
    }

    public void incrementDischargeCount(Long vacuum_id){
        this.vacuumRepository.incrementDischargeCount(vacuum_id);
    }

    public void resetDischargeCount(Long vacuum_id){
        this.vacuumRepository.resetDischargeCount(vacuum_id);
    }

    public void disableCleaner(Long user_id, Long cleaner_id){
        this.vacuumRepository.disable(user_id, cleaner_id);
    }

    public void performAction(Long vacuum_id, Long user_id, VacuumStatus vacuumStatus){
        this.backgroundTaskService.doJob(vacuum_id, user_id, vacuumStatus);
    }

//    @Scheduled(fixedDelay = 60000)
//    public void runAutoDischarge(){
//        List<Vacuum> discharge = this.vacuumRepository.getCleanersToDischarge();
//
//        for(Vacuum vacuum : discharge){
//            this.performAction(vacuum.getId(), vacuum.getAdded_by(), VacuumStatus.DISCHARGING);
//        }
//    }
}
