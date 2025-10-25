package com.github.sbcharr.user_service.controllers;

import com.github.sbcharr.user_service.dtos.SignupRequestDto;
import com.github.sbcharr.user_service.dtos.UserDto;
import com.github.sbcharr.user_service.models.User;
import com.github.sbcharr.user_service.services.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public UserDto signUp(@RequestBody SignupRequestDto requestDto) {
        User user = userService.signup(
                requestDto.getName(),
                requestDto.getEmail(),
                requestDto.getPassword()
        );
        return null;
    }
//
//    @PostMapping("/login")
//    public TokenDto login(@RequestBody LoginRequestDto requestDto) {
//        return null;
//    }
//
//    @GetMapping("/validate/{token}")
//    public UserDto validateToken(@PathVariable("token") String token) {
//        return null;
//    }
//
//    @PutMapping("/logout")
//    public boolean logOut() {
//        return false;
//    }
//
//    @GetMapping("/{id}")
//    public UserDto getUser(@PathVariable("id") Long id) {
//        User user = userService.getUser(id);
//        System.out.println(user.getEmail());
//        return from(user);
//    }
//
//    private UserDto from(User user) {
//        UserDto userDto = new UserDto();
//        userDto.setEmail(user.getEmail());
//        userDto.setRoles(null);
//        return userDto;
//    }


}
