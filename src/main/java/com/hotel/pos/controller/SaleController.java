package com.hotel.pos.controller;

import com.hotel.pos.dto.SaleRequest;
import com.hotel.pos.dto.SaleResponse;
import com.hotel.pos.model.Role;
import com.hotel.pos.service.AuthService;
import com.hotel.pos.service.SaleService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;
    private final AuthService authService;

    public SaleController(SaleService saleService, AuthService authService) {
        this.saleService = saleService;
        this.authService = authService;
    }

    @PostMapping
    public SaleResponse create(@RequestHeader("X-Auth-Token") String token, @RequestBody SaleRequest req) {
        var user = authService.requireAuth(token);
        return saleService.createSale(req, user.getUsername());
    }

    @GetMapping
    public List<SaleResponse> list(@RequestHeader("X-Auth-Token") String token,
                                    @RequestParam(required = false) String date) {
        authService.requireAuth(token);
        LocalDate d = date != null ? LocalDate.parse(date) : LocalDate.now();
        return saleService.getVentesEntre(d.atStartOfDay(), d.plusDays(1).atStartOfDay());
    }

    @GetMapping("/{id}")
    public SaleResponse getOne(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id) {
        authService.requireAuth(token);
        return saleService.getById(id);
    }

    @PatchMapping("/{id}/annuler")
    public SaleResponse annuler(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id) {
        authService.requireAuth(token); // caissier ou admin peut annuler avant cloture
        return saleService.annulerVente(id);
    }
}
