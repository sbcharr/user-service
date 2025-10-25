package com.github.sbcharr.user_service.repositories;

import com.github.sbcharr.user_service.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Token save(Token token);

    Optional<Token> findByToken(String token);

    Optional<Token> findByTokenAndExpirationAfter(String token, Instant now);
}
