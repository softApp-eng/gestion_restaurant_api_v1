package com.hotel.pos.dto;

import com.hotel.pos.model.Category;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDto {
    private Long id;
    private String nom;
    private BigDecimal prix;
    private Category categorie;
    private boolean actif;
}
