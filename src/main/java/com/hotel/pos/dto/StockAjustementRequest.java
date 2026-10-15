package com.hotel.pos.dto;

import lombok.Data;

/** Demande d'ajustement : on fixe la nouvelle valeur de stock. */
@Data
public class StockAjustementRequest {
    private int stock;
    private String motif;
}
