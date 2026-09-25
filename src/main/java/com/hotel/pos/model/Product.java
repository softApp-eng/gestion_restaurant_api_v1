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
}
