package com.vveed.permissions.repositories;

import com.vveed.permissions.domain.VacuumJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VacuumJobRepository extends JpaRepository<VacuumJob, Long> {

    @Modifying
    @Query(value = "UPDATE VacuumJob as vj SET vj.is_active = :is_active AND vj.vacuum_id = :vacuum_id", nativeQuery = true)
    void switchJob(@Param(value="is_active") boolean is_active,
                   @Param(value="vacuum_id") Long vacuum_id);
}
