package com.github.sbcharr.user_service.dtos;

import com.github.sbcharr.user_service.models.Token;
import com.github.sbcharr.user_service.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private final String accessToken;
    private final String tokenType = "Bearer";
    private final long expiresIn;
    private final UserDto user;

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
