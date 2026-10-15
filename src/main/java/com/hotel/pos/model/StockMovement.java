package com.hotel.pos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Trace d'un mouvement de stock (entree, sortie ou ajustement).
 * Sert d'historique / piste d'audit pour l'inventaire.
 */
@Entity
@Table(name = "stock_movements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String nomProduit; // snapshot au moment du mouvement

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType type;

    /** Variation appliquee au stock (positif = entree, negatif = sortie). */
    @Column(nullable = false)
    private int quantite;

    /** Stock du produit juste apres l'operation. */
    @Column(nullable = false)
    private int stockApres;

    private String motif;

    @Column(nullable = false)
    private LocalDateTime dateHeure;

    private String utilisateur;
}
