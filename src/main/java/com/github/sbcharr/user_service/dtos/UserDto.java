package com.github.sbcharr.user_service.dtos;

import com.github.sbcharr.user_service.models.Role;
import com.github.sbcharr.user_service.models.User;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class UserDto {
    private String name;
    private String email;

    @ManyToOne
    private Set<Role> roles;

    private boolean isEmailVerified;

    public static UserDto from(User user) {
        if (user == null) {
            return null;
        }

        UserDto dto = new UserDto();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles());
        dto.setEmailVerified(user.isEmailVerified());

        return dto;
    }
}
