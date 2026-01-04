package com.github.sbcharr.user_service.dtos;

import com.github.sbcharr.user_service.models.Role;
import com.github.sbcharr.user_service.models.RoleName;
import com.github.sbcharr.user_service.models.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
public class UserDto {
    private String name;
    private String email;
    private Set<RoleName> roles;
    private boolean emailVerified;

    public static UserDto from(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .emailVerified(user.isEmailVerified())
                .build();
    }
}
