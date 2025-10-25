package com.github.sbcharr.user_service.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity(name = "users")
public class User extends BaseEntity {
    private String name;
    private String email;
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
//    @JsonIgnore
    private Set<Role> roles = new HashSet<>();
}
