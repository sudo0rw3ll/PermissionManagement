package com.vveed.permissions.domain;

import com.vveed.permissions.domain.enums.VacuumStatus;
import com.vveed.permissions.services.VacuumService;

import javax.persistence.*;

@Entity
public class VacuumJob {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Version
    private Integer version = 0;

    @Enumerated(EnumType.STRING)
    private VacuumStatus action;

    private Long vacuum;

    private Long time;

    private boolean is_active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getVacuum_id() {
        return vacuum;
    }

    public void setVacuum_id(Long vacuum_id) {
        this.vacuum = vacuum_id;
    }

    public VacuumStatus getAction() {
        return action;
    }

    public Long getVacuum() {
        return vacuum;
    }

    public void setVacuum(Long vacuum) {
        this.vacuum = vacuum;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void setAction(VacuumStatus action) {
        this.action = action;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    @Override
    public String toString() {
        return "[SCHEDULE JOB] " + this.vacuum + " " + this.action;
    }
}
