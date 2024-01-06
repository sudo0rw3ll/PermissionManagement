package com.vveed.permissions.services;

import com.vveed.permissions.domain.Vacuum;
import com.vveed.permissions.domain.enums.VacuumStatus;
import com.vveed.permissions.repositories.VacuumRepository;
import com.vveed.permissions.services.background.BackgroundTaskService;
import org.springframework.beans.factory.annotation.Autowired;
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
        return this.vacuumRepository.search(user_id, name, statuses, dateFrom, dateTo);
    }

    public void disableCleaner(Long user_id, Long cleaner_id){
        this.vacuumRepository.disable(user_id, cleaner_id);
    }

    public void performAction(Long vacuum_id, Long user_id, VacuumStatus vacuumStatus){
        this.backgroundTaskService.doJob(vacuum_id, user_id, vacuumStatus);
    }
}
