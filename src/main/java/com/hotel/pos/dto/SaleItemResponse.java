package com.hotel.pos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SaleItemResponse {
    private String nomProduit;
    private BigDecimal prixUnitaire;
    private int quantite;
    private BigDecimal sousTotal;
}
