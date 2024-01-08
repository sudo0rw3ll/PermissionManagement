package com.vveed.permissions.domain;

import com.vveed.permissions.domain.enums.VacuumStatus;

import javax.persistence.*;

@Entity
public class Error {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private Long vacuum;

    private String operation;

    private Long time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getVaccum_id() {
        return vacuum;
    }

    public void setVaccum_id(Long vacuum) {
        this.vacuum = vacuum;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "[ERROR] " + this.message + " " + this.operation + " ON CLEANER WITH ID " + this.vacuum;
    }
}
