package com.github.sbcharr.user_service.repositories;

import com.github.sbcharr.user_service.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByValueAndDeletedAtIsNullAndExpiryAtAfter(String value, Instant now);
}
