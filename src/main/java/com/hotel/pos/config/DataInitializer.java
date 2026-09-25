package com.hotel.pos.config;

import com.hotel.pos.model.*;
import com.hotel.pos.repository.ProductRepository;
import com.hotel.pos.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Cree un compte admin (code : admin123), un compte caissier (code : caisse123)
 * et quelques produits de demonstration au premier demarrage.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public DataInitializer(UserRepository userRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        // Si aucun utilisateur n'existe, on les initialise avec leurs codes uniques
        if (userRepository.count() == 0) {
            // Le username stocké sert de nom d'affichage, et le mot de passe correspond au code saisi
            userRepository.save(new User(null, "Administrateur", encoder.encode("admin123"), Role.ADMIN));
            userRepository.save(new User(null, "Caissier", encoder.encode("caisse123"), Role.CAISSIER));
        }

        if (productRepository.count() == 0) {
            productRepository.save(new Product(null, "Petit dejeuner continental", new BigDecimal("50.00"), Category.PETIT_DEJEUNER, true));
            productRepository.save(new Product(null, "Petit dejeuner americain", new BigDecimal("70.00"), Category.PETIT_DEJEUNER, true));
            productRepository.save(new Product(null, "Jus d'orange", new BigDecimal("20.00"), Category.BOISSON, true));
            productRepository.save(new Product(null, "Cafe", new BigDecimal("15.00"), Category.BOISSON, true));
            productRepository.save(new Product(null, "Eau minerale 0.5L", new BigDecimal("10.00"), Category.EAU, true));
            productRepository.save(new Product(null, "Eau minerale 1.5L", new BigDecimal("15.00"), Category.EAU, true));
        }
    }
}