package com.github.sbcharr.user_service.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class JwtConfig {
    private static final Logger log = LoggerFactory.getLogger(JwtConfig.class);

    private String jwtSecret;

    public JwtConfig(@Value("${jwt.secret}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    @Bean
    public SecretKey secretKey() {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException("Property 'jwt.secret' must be set and base64-encoded");
        }
        final byte[] keyBytes; //= Decoders.BASE64.decode(jwtSecret);
        try {
            keyBytes = Decoders.BASE64.decode(jwtSecret);
        } catch (IllegalStateException ex) {
            throw new IllegalStateException("Property 'jwt.secret' must be set and base64-encoded");
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
