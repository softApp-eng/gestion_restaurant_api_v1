package com.hotel.pos.dto;

import com.hotel.pos.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Ligne d'inventaire : un produit et son niveau de stock. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItemDto {
    private Long productId;
    private String nom;
    private Category categorie;
    private BigDecimal prix;
    private boolean actif;
    private int stock;
    private int seuilAlerte;
    private boolean enAlerte;   // stock <= seuilAlerte
    private boolean rupture;    // stock <= 0
}
