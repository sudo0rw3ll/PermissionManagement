package com.vveed.permissions.domain;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(indexes={@Index(columnList="email", unique = true)})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotBlank(message = "Email for the user must be set")
    private String email;

    @Column
    @NotBlank(message = "First name of user must be set")
    private String firstName;

    @Column
    @NotBlank(message = "Last name of user must be set")
    private String lastName;

    @Column
    @NotBlank(message = "Password for user must be set")
    private String password;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_permissions",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id")
    )
    private Set<Permission> permissions = new HashSet<>();

    @OneToMany(mappedBy = "added_by")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Vacuum> cleaners;

    public Long getId(){
        return this.id;
    }

    public String getEmail(){
        return this.email;
    }

    public String getFirstName(){
        return this.firstName;
    }

    public String getLastName(){
        return this.lastName;
    }

    public String getPassword(){
        return this.password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<Permission> getPermissions() {return this.permissions;}

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public List<Vacuum> getCleaners() {
        return cleaners;
    }

    public void setCleaners(List<Vacuum> cleaners) {
        this.cleaners = cleaners;
    }

    @Override
    public String toString(){
        return "User " + this.email + " " + this.firstName + " " + this.lastName + " " + this.password;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj != null && obj instanceof User){
            User user = (User) obj;
            return this.email.equalsIgnoreCase(user.email);
        }

        return false;
    }
}
