package com.github.sbcharr.user_service.services;

import com.github.sbcharr.user_service.exceptions.InvalidTokenException;
import com.github.sbcharr.user_service.exceptions.PasswordMismatchException;
import com.github.sbcharr.user_service.exceptions.UserAlreadyExistsException;
import com.github.sbcharr.user_service.models.Token;
import com.github.sbcharr.user_service.models.User;
import com.github.sbcharr.user_service.repositories.TokenRepository;
import com.github.sbcharr.user_service.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements IUserService {
    private final TokenRepository tokenRepository;
    UserRepository userRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
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
    public Token login(String email, String password) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
                Token token = new Token();
                token.setUser(user);
                String randomToken = RandomStringUtils.randomAlphanumeric(128);
                token.setToken(randomToken);
                System.out.println(randomToken);

                ZonedDateTime utcNow = ZonedDateTime.now(ZoneOffset.UTC);
                ZonedDateTime futureDate = utcNow.plusMinutes(1);
                token.setExpiration(futureDate.toInstant());
                return tokenRepository.save(token);
            } else {
                throw new PasswordMismatchException("Invalid password");
            }
        }

        return null;
    }

    @Override
    public User validateToken(String token) {
        Optional<Token> existingToken = tokenRepository.findByTokenAndExpirationAfter(token, Instant.now());
        if (!existingToken.isPresent()) {
            log.error("Invalid token");
            throw new InvalidTokenException("Invalid token");
        }
        return existingToken.get().getUser();
    }
}
