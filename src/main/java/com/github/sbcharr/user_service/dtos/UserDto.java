package com.github.sbcharr.user_service.dtos;

import com.github.sbcharr.user_service.models.User;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDto {
    private String name;
    private String email;

    public static UserDto from(User user) {
        UserDto dto = new UserDto();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
