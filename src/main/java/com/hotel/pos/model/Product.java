package com.hotel.pos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private BigDecimal prix;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category categorie;

    @Column(nullable = false)
    private boolean actif = true;

    // ---- Inventaire ----

    /** Quantite disponible en stock. */
    @Column(nullable = false, columnDefinition = "int default 0")
    private int stock = 0;

    /** Seuil a partir duquel le produit est signale "en alerte". */
    @Column(nullable = false, columnDefinition = "int default 10")
    private int seuilAlerte = 10;
}
