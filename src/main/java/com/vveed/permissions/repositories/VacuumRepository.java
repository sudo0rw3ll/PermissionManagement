package com.vveed.permissions.repositories;

import com.vveed.permissions.domain.Vacuum;
import com.vveed.permissions.domain.enums.VacuumStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface VacuumRepository extends JpaRepository<Vacuum, Long> {

    @Query(value = "SELECT * FROM Vacuum AS vac WHERE vac.added_by = :user_id " +
            "AND (vac.name like %:name% " +
            "OR vac.status IN :statuses " +
            "OR (vac.date_created >= :dateFrom OR vac.date_created <= :dateTo))"
            ,nativeQuery = true)
    List<Vacuum> search(@Param("user_id") Long user_id,
                        @Param("name") String name,
                        @Param("statuses") List<String> statuses,
                        @Param("dateFrom") Long dateFrom,
                        @Param("dateTo") Long dateTo
    );

    @Modifying
    @Transactional
    @Query(value = "UPDATE Vacuum AS vac SET vac.status = :status WHERE vac.added_by = :user_id AND vac.id = :vac_id")
    void update(@Param("status") VacuumStatus vacuumStatus, @Param("user_id") Long user_id, @Param("vac_id") Long vac_id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Vacuum AS vac SET vac.active = 0 WHERE vac.added_by = :user_id AND vac.id = :vac_id")
    void disable(@Param("user_id") Long user_id, @Param("vac_id") Long vac_id);
}
