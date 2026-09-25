package com.hotel.pos.controller;

import com.hotel.pos.dto.ClosingResponse;
import com.hotel.pos.model.Role;
import com.hotel.pos.service.AuthService;
import com.hotel.pos.service.CashRegisterService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cash-register")
public class CashRegisterController {

    private final CashRegisterService cashRegisterService;
    private final AuthService authService;

    public CashRegisterController(CashRegisterService cashRegisterService, AuthService authService) {
        this.cashRegisterService = cashRegisterService;
        this.authService = authService;
    }

    // Cloture manuelle (en plus de la cloture automatique planifiee), reservee a l'admin
    @PostMapping("/close")
    public ClosingResponse closeNow(@RequestHeader("X-Auth-Token") String token) {
        authService.requireRole(token, Role.ADMIN);
        return cashRegisterService.closeRegister(false);
    }

    @GetMapping("/closings")
    public List<ClosingResponse> history(@RequestHeader("X-Auth-Token") String token) {
        authService.requireRole(token, Role.ADMIN);
        return cashRegisterService.getHistorique();
    }
}
