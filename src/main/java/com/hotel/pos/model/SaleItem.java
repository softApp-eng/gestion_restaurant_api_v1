package com.hotel.pos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "sale_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    private Long productId;

    @Column(nullable = false)
    private String nomProduit; // snapshot au moment de la vente

    @Column(nullable = false)
    private BigDecimal prixUnitaire; // snapshot

    @Column(nullable = false)
    private int quantite;

    @Column(nullable = false)
    private BigDecimal sousTotal;
}
