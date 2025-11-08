package com.github.sbcharr.user_service.dtos;

import com.github.sbcharr.user_service.models.Token;
import com.github.sbcharr.user_service.models.User;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class LoginResponseDto {
    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private UserDto user;

    public LoginResponseDto(String accessToken, long expiresIn, UserDto user) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public static LoginResponseDto from(Token token) {
        if (token == null) {
            return null;
        }
        String accessToken = token.getValue();
        long expiresIn = token.getExpiryAt().getEpochSecond() - Instant.now().getEpochSecond();
        User user = token.getUser();

        return new LoginResponseDto(accessToken, expiresIn, UserDto.from(user));
    }
}
