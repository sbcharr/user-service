package com.github.sbcharr.user_service.repositories;

import com.github.sbcharr.user_service.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
}
