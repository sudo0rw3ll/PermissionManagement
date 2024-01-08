package com.vveed.permissions.repositories;

import com.vveed.permissions.domain.Error;
import com.vveed.permissions.domain.VacuumJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ErrorsRepository extends JpaRepository<Error, Long> {
    @Query(value = "SELECT * FROM error as er LEFT JOIN Vacuum as vc ON er.vacuum = vc.id WHERE vc.added_by = :user_id", nativeQuery = true)
    List<Error> findAll(@Param(value="user_id") Long user_id);
}
