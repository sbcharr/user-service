
package com.github.sbcharr.user_service.services;

import com.github.sbcharr.user_service.dtos.AuthUserDetails;
import com.github.sbcharr.user_service.exceptions.InvalidCredentialsException;
import com.github.sbcharr.user_service.exceptions.InvalidTokenException;
import com.github.sbcharr.user_service.exceptions.UserAlreadyExistsException;
import com.github.sbcharr.user_service.models.Role;
import com.github.sbcharr.user_service.models.RoleName;
import com.github.sbcharr.user_service.models.Token;
import com.github.sbcharr.user_service.models.User;
import com.github.sbcharr.user_service.repositories.RoleRepository;
import com.github.sbcharr.user_service.repositories.TokenRepository;
import com.github.sbcharr.user_service.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final RedisTemplate<String, String> redisTemplate;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SecretKey secretKey;

    @Value("${jwt.blacklist.prefix}")
    private String jwtBlacklistPrefix;

    @Value("${jwt.expiry.minutes}")
    private int jwtExpiryMinutes;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder, TokenRepository tokenRepository,
                           SecretKey secretKey, RedisTemplate<String, String> redisTemplate) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
        this.secretKey = secretKey;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public User register(String name, String email, String password) {
        log.info("Register attempt: {}", email);
        // Check if user with email already exists
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("Email already registered");
        }
        // add default role
        Role buyerRole = roleRepository.findByName(RoleName.BUYER)
                .orElseThrow(() -> new RuntimeException("Default role BUYER not found"));
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setHashedPassword(bCryptPasswordEncoder.encode(password));
        user.getRoles().add(buyerRole);

        User savedUser = userRepository.save(user);
        log.info("User registered: id={}, email={}", savedUser.getId(), email);

        return savedUser;
    }

    @Override
    public boolean verifyEmail(String token) {
        log.info("Verifying email with token: {}", token.substring(0, 8) + "...");
        Optional<AuthUserDetails> authUserDetails = validateToken(token);
        if (authUserDetails.isEmpty()) {
            throw new InvalidTokenException("Invalid or expired token");
        }

        // single-use token: logout to blacklist
        logout(token);

        User user = userRepository.findById(authUserDetails.get().getId())
                .orElseThrow(() -> new InvalidTokenException("User not found"));

        if (user.isEmailVerified()) {
            log.warn("Email already verified for user: {}", user.getId());
            return false;
        }
        user.setEmailVerified(true);
        userRepository.save(user);

        return true;
    }

    @Override
    public Token login(String email, String password) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!bCryptPasswordEncoder.matches(password, user.getHashedPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        ZonedDateTime utcNow = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime futureDate = utcNow.plusMinutes(jwtExpiryMinutes);

        String jwtToken = Jwts.builder()
                .subject(user.getEmail())
                .issuer(jwtIssuer)
                .issuedAt(Date.from(utcNow.toInstant()))
                .expiration(Date.from(futureDate.toInstant()))
                .claim("userId", user.getId())
                .claim("roles", user.getRoles().stream()
                        .map(Role::getName)
                        .map(RoleName::getValue)
                        .collect(Collectors.toList()))
                .signWith(secretKey)
                .compact();

        Token token = new Token();
        token.setUser(user);
        token.setValue(jwtToken);
        token.setExpiryAt(futureDate.toInstant());

        tokenRepository.save(token);
        return token;
    }

    @Override
    public Optional<AuthUserDetails> validateToken(String token) {
        log.debug("Validating token: {}", token.substring(0, 8) + "...");

        try {
            Claims claims = parseJWT(token);
            Long userId = claims.get("userId", Long.class);
            if (userId == null) {
                log.warn("Token missing userId claim");
                return Optional.empty();
            }
            // 2. Redis blacklist check
            String redisKey = jwtBlacklistPrefix + token;
            Boolean isBlacklisted = redisTemplate.hasKey(redisKey);
            if (Boolean.TRUE.equals(isBlacklisted)) {
                log.warn("Token is blacklisted in Redis: {}", redisKey);
                return Optional.empty();
            }

            return Optional.of(AuthUserDetails.fromClaims(claims));
        } catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", token.substring(0, 8));
            return Optional.empty();
        } catch (JwtException e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected validation error", e);
            return Optional.empty();
        }
    }

    @Override
    public boolean logout(String token) {
        Claims claims = parseJWT(token);
        String redisKey = jwtBlacklistPrefix + token;

        Instant tokenExpiry = claims.getExpiration().toInstant();
        long ttlSeconds = Math.max(0, ChronoUnit.SECONDS.between(Instant.now(), tokenExpiry));

        // save to redis blacklist
        redisTemplate.opsForValue().set(redisKey, "revoked", ttlSeconds);

        // soft-delete token in database
        tokenRepository.findByValueAndDeletedAtIsNull(token)
                .ifPresent(t -> {
                    t.setDeletedAt(Instant.now());
                    tokenRepository.save(t);
                });

        return true;
    }

    private Claims parseJWT(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            log.error("JWT parse failed: {}", e.getMessage());
            throw new InvalidTokenException("Invalid token format");
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findByIdAndDeletedAtIsNull(id);
    }
}
