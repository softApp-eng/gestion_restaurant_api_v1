package com.hotel.pos.dto;

import lombok.Data;

/** Demande de modification du seuil d'alerte d'un produit. */
@Data
public class SeuilRequest {
    private int seuilAlerte;
}
