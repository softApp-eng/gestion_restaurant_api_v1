package com.hotel.pos.controller;

import com.hotel.pos.dto.*;
import com.hotel.pos.model.Role;
import com.hotel.pos.service.AuthService;
import com.hotel.pos.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final AuthService authService;

    public InventoryController(InventoryService inventoryService, AuthService authService) {
        this.inventoryService = inventoryService;
        this.authService = authService;
    }

    /** Etat de l'inventaire (accessible a tout utilisateur connecte). */
    @GetMapping
    public List<InventoryItemDto> getAll(@RequestHeader("X-Auth-Token") String token) {
        authService.requireAuth(token);
        return inventoryService.getAll();
    }

    /** Historique des mouvements de stock. */
    @GetMapping("/movements")
    public List<StockMovementDto> movements(@RequestHeader("X-Auth-Token") String token,
                                            @RequestParam(defaultValue = "100") int limit) {
        authService.requireAuth(token);
        return inventoryService.movements(limit);
    }

    @PostMapping("/{productId}/entree")
    public InventoryItemDto entree(@RequestHeader("X-Auth-Token") String token,
                                   @PathVariable Long productId,
                                   @RequestBody StockActionRequest req) {
        var user = authService.requireRole(token, Role.ADMIN);
        return inventoryService.entree(productId, req.getQuantite(), req.getMotif(), user.getUsername());
    }

    @PostMapping("/{productId}/sortie")
    public InventoryItemDto sortie(@RequestHeader("X-Auth-Token") String token,
                                   @PathVariable Long productId,
                                   @RequestBody StockActionRequest req) {
        var user = authService.requireRole(token, Role.ADMIN);
        return inventoryService.sortie(productId, req.getQuantite(), req.getMotif(), user.getUsername());
    }

    @PostMapping("/{productId}/ajustement")
    public InventoryItemDto ajustement(@RequestHeader("X-Auth-Token") String token,
                                       @PathVariable Long productId,
                                       @RequestBody StockAjustementRequest req) {
        var user = authService.requireRole(token, Role.ADMIN);
        return inventoryService.ajuster(productId, req.getStock(), req.getMotif(), user.getUsername());
    }

    @PatchMapping("/{productId}/seuil")
    public InventoryItemDto seuil(@RequestHeader("X-Auth-Token") String token,
                                  @PathVariable Long productId,
                                  @RequestBody SeuilRequest req) {
        authService.requireRole(token, Role.ADMIN);
        return inventoryService.setSeuil(productId, req.getSeuilAlerte());
    }
}
