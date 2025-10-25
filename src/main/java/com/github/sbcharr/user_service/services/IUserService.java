package com.github.sbcharr.user_service.services;

import com.github.sbcharr.user_service.exceptions.UserAlreadyExistsException;
import com.github.sbcharr.user_service.models.Token;
import com.github.sbcharr.user_service.models.User;

public interface IUserService {
    User signup(String name, String email, String password) throws UserAlreadyExistsException;

    Token login(String username, String password);

    User validateToken(String token);
}
