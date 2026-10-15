package com.hotel.pos.dto;

import lombok.Data;

/** Demande d'entree ou de sortie de stock. */
@Data
public class StockActionRequest {
    private int quantite;
    private String motif;
}
