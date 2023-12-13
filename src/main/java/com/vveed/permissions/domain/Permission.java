package com.vveed.permissions.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(indexes = {@Index(columnList = "permission", unique = true)})
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String permission;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "permissions")
    @JsonIgnore
    private Set<User> users = new HashSet<>();

    public Long getId(){
        return this.id;
    }

    public String getPermission(){
        return this.permission;
    }

    public Set<User> getUsers(){
        return this.users;
    }

    public void addUser(User user) {
        if (!this.users.contains(user)){
            this.users.add(user);
            user.getPermissions().add(this);
        }
    }

    public void removeUser(User user){
        this.users.remove(user);
        user.getPermissions().remove(this);
    }

    @Override
    public String toString(){
        return "Permission " + this.permission;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj != null && obj instanceof Permission){
            Permission perm = (Permission) obj;
            return this.permission.equalsIgnoreCase(perm.permission);
        }

        return false;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
