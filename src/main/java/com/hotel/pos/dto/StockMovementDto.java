package com.hotel.pos.dto;

import com.hotel.pos.model.MovementType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Mouvement de stock expose a l'interface. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementDto {
    private Long id;
    private Long productId;
    private String nomProduit;
    private MovementType type;
    private int quantite;
    private int stockApres;
    private String motif;
    private LocalDateTime dateHeure;
    private String utilisateur;
}
