package com.github.sbcharr.user_service.controllers;

import com.github.sbcharr.user_service.dtos.*;
import com.github.sbcharr.user_service.exceptions.InvalidTokenException;
import com.github.sbcharr.user_service.models.Token;
import com.github.sbcharr.user_service.models.User;
import com.github.sbcharr.user_service.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> signUp(@RequestBody SignupRequestDto requestDto) {
        User user = userService.register(
                requestDto.getName(),
                requestDto.getEmail(),
                requestDto.getPassword()
        );

        log.info("User signed up: id={}", user.getId());

        return ResponseEntity.ok(UserDto.from(user));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        Token token = userService.login(
                requestDto.getEmail(),
                requestDto.getPassword()
        );

        return ResponseEntity.ok(LoginResponseDto.from(token));
    }

    @GetMapping("/validate")
    public ResponseEntity<UserDto> validateTokenFromHeader(@RequestHeader(name = "Authorization", required = false)
                                                               String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String token = authorization.substring("Bearer ".length());
        User user = userService.validateToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        return ResponseEntity.ok(UserDto.from(user));
    }

    @GetMapping("/sample")
    public ResponseEntity<Void> sampleAPI() {
        log.info("Received a call from ProductService!");
        return ResponseEntity.ok().build();
    }

    // TODO: Implement logout functionality
//    @PutMapping("/logout")
//    public boolean logOut() {
//        return false;
//    }

}
