package com.hotel.pos.service;

import com.hotel.pos.dto.ProductDto;
import com.hotel.pos.model.Product;
import com.hotel.pos.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDto> getAll(boolean includeInactifs) {
        List<Product> products = includeInactifs ? productRepository.findAll() : productRepository.findByActifTrue();
        return products.stream().map(this::toDto).collect(Collectors.toList());
    }

    public ProductDto create(ProductDto dto) {
        Product p = new Product();
        p.setNom(dto.getNom());
        p.setPrix(dto.getPrix());
        p.setCategorie(dto.getCategorie());
        p.setActif(true);
        return toDto(productRepository.save(p));
    }

    public ProductDto update(Long id, ProductDto dto) {
        Product p = find(id);
        p.setNom(dto.getNom());
        p.setPrix(dto.getPrix());
        p.setCategorie(dto.getCategorie());
        return toDto(productRepository.save(p));
    }

    // "annulation" d'un petit dejeuner / produit = desactivation (on garde l'historique des ventes)
    public ProductDto annuler(Long id) {
        Product p = find(id);
        p.setActif(false);
        return toDto(productRepository.save(p));
    }

    public ProductDto reactiver(Long id) {
        Product p = find(id);
        p.setActif(true);
        return toDto(productRepository.save(p));
    }

    private Product find(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produit introuvable"));
    }

    private ProductDto toDto(Product p) {
        ProductDto dto = new ProductDto();
        dto.setId(p.getId());
        dto.setNom(p.getNom());
        dto.setPrix(p.getPrix());
        dto.setCategorie(p.getCategorie());
        dto.setActif(p.isActif());
        return dto;
    }
}
