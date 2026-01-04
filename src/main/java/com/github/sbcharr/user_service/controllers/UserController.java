package com.github.sbcharr.user_service.controllers;

import com.github.sbcharr.user_service.dtos.AuthUserDetails;
import com.github.sbcharr.user_service.dtos.UserDto;
import com.github.sbcharr.user_service.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getUserProfile(Authentication auth) {
        AuthUserDetails jwtUser = (AuthUserDetails) auth.getPrincipal();

        UserDto dto = userService.findById(jwtUser.getId())
                .map(UserDto::from)
                .orElseThrow(() -> {
                    log.error("User not found: id={}", jwtUser.getId());
                    return new UsernameNotFoundException("User not found");
                });
        return ResponseEntity.ok(dto);
    }
}
