package com.github.sbcharr.user_service.dtos;

import com.github.sbcharr.user_service.models.Role;
import com.github.sbcharr.user_service.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
public class UserDto {
    private String name;
    private String email;
    private String password;
    private Set<Role> roles = new HashSet<>();

    public static UserDto from(User user) {
        UserDto dto = new UserDto();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        return dto;
    }
}
