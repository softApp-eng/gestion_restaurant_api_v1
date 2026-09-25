package com.hotel.pos.controller;

import com.hotel.pos.dto.LoginRequest;
import com.hotel.pos.dto.LoginResponse;
import com.hotel.pos.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader(value = "X-Auth-Token", required = false) String token) {
        authService.logout(token);
    }
}
