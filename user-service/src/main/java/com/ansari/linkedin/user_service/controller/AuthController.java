package com.ansari.linkedin.user_service.controller;

import com.ansari.linkedin.user_service.dto.LoginRequestDto;
import com.ansari.linkedin.user_service.dto.SignUpRequestDto;
import com.ansari.linkedin.user_service.dto.UserDto;
import com.ansari.linkedin.user_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto){
        UserDto userDto = userService.signUp(signUpRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequestDto){
        String token = userService.login(loginRequestDto);
        return ResponseEntity.ok(token);
    }

}
