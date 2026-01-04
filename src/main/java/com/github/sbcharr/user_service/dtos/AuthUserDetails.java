package com.github.sbcharr.user_service.dtos;

import com.github.sbcharr.user_service.models.RoleName;
import io.jsonwebtoken.Claims;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
public class AuthUserDetails {
    private final Long id;
    private final String email;
    private final Set<RoleName> roles;

    public static AuthUserDetails fromClaims(Claims claims) {
        @SuppressWarnings("unchecked")
        Set<String> roleNames = claims.get("roles", Set.class);
        Set<RoleName> roles = roleNames.stream()
                .map(RoleName::fromValue)
                .collect(Collectors.toSet());

        return AuthUserDetails.builder()
                .id(claims.get("userId", Long.class))
                .email(claims.getSubject())
                .roles(roles)
                .build();
    }
}
