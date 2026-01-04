package com.github.sbcharr.user_service.services;

import com.github.sbcharr.user_service.dtos.AuthUserDetails;
import com.github.sbcharr.user_service.models.Token;
import com.github.sbcharr.user_service.models.User;

import java.util.Optional;

public interface UserService {
    User register(String name, String email, String password);

    Token login(String email, String password);

    Optional<AuthUserDetails> validateToken(String token);

    default boolean logout(String token) {
        // Default implementation does nothing
        return false;
    }
}
