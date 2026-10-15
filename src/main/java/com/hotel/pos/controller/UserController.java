package com.hotel.pos.controller;

import com.hotel.pos.dto.CreateUserRequest;
import com.hotel.pos.dto.UpdateActifRequest;
import com.hotel.pos.dto.UpdateCodeRequest;
import com.hotel.pos.dto.UserDto;
import com.hotel.pos.model.Role;
import com.hotel.pos.service.AuthService;
import com.hotel.pos.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Gestion des comptes (réservée à l'administrateur). */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping
    public List<UserDto> getAll(@RequestHeader("X-Auth-Token") String token) {
        authService.requireRole(token, Role.ADMIN);
        return userService.getAll();
    }

    @PostMapping
    public UserDto create(@RequestHeader("X-Auth-Token") String token, @RequestBody CreateUserRequest req) {
        authService.requireRole(token, Role.ADMIN);
        return userService.create(req);
    }

    @PatchMapping("/{id}/actif")
    public UserDto setActif(@RequestHeader("X-Auth-Token") String token,
                            @PathVariable Long id,
                            @RequestBody UpdateActifRequest req) {
        var actor = authService.requireRole(token, Role.ADMIN);
        return userService.setActif(id, req.isActif(), actor.getId());
    }

    @PostMapping("/{id}/code")
    public UserDto resetCode(@RequestHeader("X-Auth-Token") String token,
                             @PathVariable Long id,
                             @RequestBody UpdateCodeRequest req) {
        authService.requireRole(token, Role.ADMIN);
        return userService.resetCode(id, req.getCode());
    }
}
