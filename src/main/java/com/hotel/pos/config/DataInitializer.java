package com.hotel.pos.config;

import com.hotel.pos.model.*;
import com.hotel.pos.repository.ProductRepository;
import com.hotel.pos.repository.StockMovementRepository;
import com.hotel.pos.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Cree un compte admin (code : admin123), un compte caissier (code : caisse123)
 * et quelques produits de demonstration au premier demarrage.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public DataInitializer(UserRepository userRepository, ProductRepository productRepository,
                           StockMovementRepository stockMovementRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    @Override
    public void run(String... args) {
        // Si aucun utilisateur n'existe, on les initialise avec leurs codes uniques
        if (userRepository.count() == 0) {
            // Le username stocké sert de nom d'affichage, et le mot de passe correspond au code saisi
            userRepository.save(new User(null, "Administrateur", encoder.encode("admin123"), Role.ADMIN, true, LocalDateTime.now()));
            userRepository.save(new User(null, "Caissier", encoder.encode("caisse123"), Role.CAISSIER, true, LocalDateTime.now()));
            userRepository.save(new User(null, "Reception", encoder.encode("recep123"), Role.RECEPTIONNISTE, true, LocalDateTime.now()));
        }

        // Garantit la presence d'un compte receptionniste (bases existantes)
        if (userRepository.findAll().stream().noneMatch(u -> u.getRole() == Role.RECEPTIONNISTE)) {
            userRepository.save(new User(null, "Reception", encoder.encode("recep123"), Role.RECEPTIONNISTE, true, LocalDateTime.now()));
        }

        if (productRepository.count() == 0) {
            productRepository.save(new Product(null, "Petit dejeuner continental", new BigDecimal("50.00"), Category.PETIT_DEJEUNER, true, 25, 10));
            productRepository.save(new Product(null, "Petit dejeuner americain", new BigDecimal("70.00"), Category.PETIT_DEJEUNER, true, 20, 10));
            productRepository.save(new Product(null, "Jus d'orange", new BigDecimal("20.00"), Category.BOISSON, true, 40, 15));
            productRepository.save(new Product(null, "Cafe", new BigDecimal("15.00"), Category.BOISSON, true, 60, 20));
            productRepository.save(new Product(null, "Eau minerale 0.5L", new BigDecimal("10.00"), Category.EAU, true, 80, 20));
            productRepository.save(new Product(null, "Eau minerale 1.5L", new BigDecimal("15.00"), Category.EAU, true, 50, 15));
        }

        // Première utilisation de l'inventaire : on amorce un niveau de stock realiste
        // pour les produits deja presents qui n'ont encore aucun suivi (stock = 0).
        if (stockMovementRepository.count() == 0) {
            for (Product p : productRepository.findAll()) {
                if (p.getStock() <= 0) {
                    p.setStock(stockParDefaut(p.getCategorie()));
                    p.setSeuilAlerte(seuilParDefaut(p.getCategorie()));
                    productRepository.save(p);
                }
            }
        }
    }

    private int stockParDefaut(Category c) {
        switch (c) {
            case PETIT_DEJEUNER: return 20;
            case BOISSON: return 40;
            default: return 60; // EAU
        }
    }

    private int seuilParDefaut(Category c) {
        switch (c) {
            case PETIT_DEJEUNER: return 10;
            case BOISSON: return 15;
            default: return 20; // EAU
        }
    }
}