package com.github.sbcharr.user_service.repositories;

import com.github.sbcharr.user_service.models.Role;
import com.github.sbcharr.user_service.models.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
