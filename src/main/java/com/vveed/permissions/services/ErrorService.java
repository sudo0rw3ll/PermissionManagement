package com.vveed.permissions.services;

import com.vveed.permissions.domain.Error;
import com.vveed.permissions.repositories.ErrorsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ErrorService implements IService<Error, Long>{

    private final ErrorsRepository errorsRepository;

    public ErrorService(ErrorsRepository errorsRepository){
        this.errorsRepository = errorsRepository;
    }

    @Override
    public List<Error> findAll() {
        return this.errorsRepository.findAll();
    }

    public List<Error> findAll(Long userId){
        return this.errorsRepository.findAll(userId);
    }

    @Override
    public Optional<Error> findById(Long id) {
        return this.errorsRepository.findById(id);
    }

    @Override
    public Error save(Error error) {
        return this.errorsRepository.save(error);
    }

    @Override
    public void deleteById(Long id) {
        this.errorsRepository.deleteById(id);
    }
}
