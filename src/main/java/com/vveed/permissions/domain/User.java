package com.vveed.permissions.domain;

import javax.persistence.*;
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
    private String email;
    private String firstName;
    private String lastName;
    private String password;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_permissions",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id")
    )
    private Set<Permission> permissions = new HashSet<>();

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

    public Set<Permission> getPermissions() {return this.permissions;}

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
