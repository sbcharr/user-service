package com.github.sbcharr.user_service.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity(name = "roles")
public class Role extends BaseEntity {
    private String role;
}
