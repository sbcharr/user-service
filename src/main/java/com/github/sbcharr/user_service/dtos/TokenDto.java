package com.github.sbcharr.user_service.dtos;

import com.github.sbcharr.user_service.models.Token;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class TokenDto {
    private String token;
    private Instant tokenExpiration;
    private String email;

    public static TokenDto from(Token token) {
        if (token == null) {
            return null;
        }
        TokenDto tokenDto = new TokenDto();
        tokenDto.token = token.getToken();
        tokenDto.tokenExpiration = token.getExpiration();
        tokenDto.email = token.getUser().getEmail();

        return tokenDto;
    }
}
