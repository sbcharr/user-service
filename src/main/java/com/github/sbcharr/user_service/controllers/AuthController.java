package com.github.sbcharr.user_service.controllers;

import com.github.sbcharr.user_service.dtos.LoginRequestDto;
import com.github.sbcharr.user_service.dtos.LoginResponseDto;
import com.github.sbcharr.user_service.dtos.SignupRequestDto;
import com.github.sbcharr.user_service.dtos.UserDto;
import com.github.sbcharr.user_service.models.Token;
import com.github.sbcharr.user_service.models.User;
import com.github.sbcharr.user_service.services.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth")
public class AuthController {
    private UserServiceImpl userServiceImpl;

    public AuthController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignupRequestDto requestDto) {
        User user = userServiceImpl.register(
                requestDto.getName(),
                requestDto.getEmail(),
                requestDto.getPassword()
        );

        log.info("User signed up: id={}", user.getId());

        return ResponseEntity.ok(UserDto.from(user));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto requestDto) {
        log.info("Login attempt: {}", requestDto.email());
        Token token = userServiceImpl.login(
                requestDto.email(),
                requestDto.password()
        );
        log.info("Login success: userId={}", token.getUser().getId());
        return ResponseEntity.ok(LoginResponseDto.from(token));
    }

    @GetMapping("/sample")
    public ResponseEntity<Void> sampleAPI(HttpServletRequest request) {
        log.info("Sample ping from: {}", request.getRemoteAddr());
        return ResponseEntity.ok()
                .header("X-Service-Version", "1.0.0")
                .header("X-Service-Status", "healthy")
                .build();
    }

    @PutMapping("/logout")
    public ResponseEntity<Void> logOut(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }
        String token = authHeader.substring(7);
        boolean result = userServiceImpl.logout(token);

        return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}
