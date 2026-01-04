package com.github.sbcharr.user_service.controllers;

import com.github.sbcharr.user_service.dtos.*;
import com.github.sbcharr.user_service.exceptions.InvalidTokenException;
import com.github.sbcharr.user_service.models.Token;
import com.github.sbcharr.user_service.models.User;
import com.github.sbcharr.user_service.services.UserServiceImpl;
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

    @PostMapping("/register")
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
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        Token token = userServiceImpl.login(
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
        User user = userServiceImpl.validateToken(token)
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
