package com.github.sbcharr.user_service.controllers;

import com.github.sbcharr.user_service.dtos.*;
import com.github.sbcharr.user_service.exceptions.InvalidTokenException;
import com.github.sbcharr.user_service.models.Token;
import com.github.sbcharr.user_service.models.User;
import com.github.sbcharr.user_service.services.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

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
    public boolean logOut() {
        return false;
    }
    

}
