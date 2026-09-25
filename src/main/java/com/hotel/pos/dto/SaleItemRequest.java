package com.hotel.pos.dto;

import lombok.Data;

@Data
public class SaleItemRequest {
    private Long productId;
    private int quantite;
}
