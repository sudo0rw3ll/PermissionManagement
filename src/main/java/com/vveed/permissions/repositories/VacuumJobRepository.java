package com.vveed.permissions.repositories;

import com.vveed.permissions.domain.VacuumJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacuumJobRepository extends JpaRepository<VacuumJob, Long> {

    @Modifying
    @Query(value = "UPDATE VacuumJob as vj SET vj.is_active = :is_active AND vj.vacuum_id = :vacuum_id", nativeQuery = true)
    void switchJob(@Param(value="is_active") boolean is_active,
                   @Param(value="vacuum_id") Long vacuum_id);

    @Query(value = "SELECT * FROM vacuum_job as vj LEFT JOIN Vacuum as vc ON vj.vacuum = vc.id WHERE vc.added_by = :user_id", nativeQuery = true)
    List<VacuumJob> findAll(@Param(value="user_id") Long user_id);
}
