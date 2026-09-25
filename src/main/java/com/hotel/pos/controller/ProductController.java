package com.hotel.pos.controller;

import com.hotel.pos.dto.ProductDto;
import com.hotel.pos.model.Role;
import com.hotel.pos.service.AuthService;
import com.hotel.pos.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final AuthService authService;

    public ProductController(ProductService productService, AuthService authService) {
        this.productService = productService;
        this.authService = authService;
    }

    @GetMapping
    public List<ProductDto> getAll(@RequestHeader("X-Auth-Token") String token,
                                    @RequestParam(defaultValue = "false") boolean all) {
        var user = authService.requireAuth(token);
        boolean includeInactifs = all && user.getRole() == Role.ADMIN;
        return productService.getAll(includeInactifs);
    }

    @PostMapping
    public ProductDto create(@RequestHeader("X-Auth-Token") String token, @RequestBody ProductDto dto) {
        authService.requireRole(token, Role.ADMIN);
        return productService.create(dto);
    }

    @PutMapping("/{id}")
    public ProductDto update(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id, @RequestBody ProductDto dto) {
        authService.requireRole(token, Role.ADMIN);
        return productService.update(id, dto);
    }

    // Annulation d'un petit dejeuner / produit (desactivation, ne peut plus etre vendu)
    @PatchMapping("/{id}/annuler")
    public ProductDto annuler(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id) {
        authService.requireRole(token, Role.ADMIN);
        return productService.annuler(id);
    }

    @PatchMapping("/{id}/activer")
    public ProductDto activer(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id) {
        authService.requireRole(token, Role.ADMIN);
        return productService.reactiver(id);
    }
}
