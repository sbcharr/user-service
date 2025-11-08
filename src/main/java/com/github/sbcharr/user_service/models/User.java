package com.github.sbcharr.user_service.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity(name = "users")
public class User extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "hashed_password", nullable = false)
    private String hashedPassword;

    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "users_roles",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "role_id")
//    )
    private Set<Role> roles = new HashSet<>();

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;
}
