package com.github.sbcharr.user_service.repositories;

import com.github.sbcharr.user_service.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}
