package com.github.sbcharr.user_service.services;

import com.github.sbcharr.user_service.exceptions.UserAlreadyExistsException;
import com.github.sbcharr.user_service.models.Token;
import com.github.sbcharr.user_service.models.User;
import com.github.sbcharr.user_service.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService {
    UserRepository userRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User signup(String name, String email, String password) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("Email already registered");
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        return userRepository.save(user);
    }

    @Override
    public Token login(String username, String password) {
        return null;
    }

    @Override
    public User validateToken(String token) {
        return null;
    }
}
