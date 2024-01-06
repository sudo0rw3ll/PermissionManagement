package com.vveed.permissions.domain;

import com.vveed.permissions.domain.enums.VacuumStatus;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
public class Vacuum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long dateCreated;

    @Version
    private Integer version = 0;

    @Column
    @NotBlank(message = "Vacuum cleaner name is mandatory")
    private String name;

    @Enumerated(EnumType.STRING)
    private VacuumStatus status;

    private boolean active;

    private Long added_by;

    @OneToMany(mappedBy = "vacuum")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<VacuumJob> jobs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VacuumStatus getStatus() {
        return status;
    }

    public void setStatus(VacuumStatus status) {
        this.status = status;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getAdded_by() {
        return added_by;
    }

    public void setAdded_by(Long added_by) {
        this.added_by = added_by;
    }

    public Long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return this.added_by + " " + this.active + " " + this.status.toString();
    }
}
