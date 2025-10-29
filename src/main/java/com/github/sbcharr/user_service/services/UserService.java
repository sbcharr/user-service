
package com.github.sbcharr.user_service.services;

import com.github.sbcharr.user_service.exceptions.InvalidCredentialsException;
import com.github.sbcharr.user_service.exceptions.InvalidTokenException;
import com.github.sbcharr.user_service.exceptions.UserAlreadyExistsException;
import com.github.sbcharr.user_service.models.Token;
import com.github.sbcharr.user_service.models.User;
import com.github.sbcharr.user_service.repositories.TokenRepository;
import com.github.sbcharr.user_service.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements IUserService {
    private TokenRepository tokenRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private SecretKey secretKey;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder, TokenRepository tokenRepository,
                       SecretKey secretKey) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
        this.secretKey = secretKey;
    }

    @Override
    public User signup(String name, String email, String password) {
        // Check if user with email already exists
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
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        ZonedDateTime utcNow = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime futureDate = utcNow.plusMinutes(30);

        String jwtToken = Jwts.builder()
                .subject(user.getEmail())
                .issuer("sbcharr.github.com")
                .issuedAt(Date.from(utcNow.toInstant()))
                .expiration(Date.from(futureDate.toInstant()))
                .claim("userId", user.getId())
                .claim("roles", user.getRoles())
                .signWith(secretKey)
                .compact();

        Token token = new Token();
        token.setUser(user);
        token.setToken(jwtToken);
        token.setExpiration(futureDate.toInstant());

        return token;
    }

    @Override
    public User validateToken(String token) {
        try {
            // parse and verify the JWT
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Long userId = claims.get("userId", Long.class);
            return userRepository.findById(userId).orElseThrow(() -> new InvalidTokenException("Invalid token"));
        } catch (ExpiredJwtException ex) {
            throw new InvalidTokenException("Token has expired");
        } catch (SignatureException ex) {
            throw new InvalidTokenException("Invalid token signature");
        } catch (JwtException ex) {
            throw new InvalidTokenException("Invalid token");
        }
    }
}
