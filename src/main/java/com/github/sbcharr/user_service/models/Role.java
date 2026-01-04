package com.github.sbcharr.user_service.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity(name = "roles")
@Table(indexes = {
        @Index(name = "idx_roles_name", columnList = "name", unique = true)
})
public class Role extends BaseEntity {
    @Column(name = "name", nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleName name;
}
