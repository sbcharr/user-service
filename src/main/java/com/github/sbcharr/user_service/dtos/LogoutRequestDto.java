package com.github.sbcharr.user_service.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LogoutRequestDto {
    private String email;
}
