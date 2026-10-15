package com.hotel.pos.dto;

import lombok.Data;

/** Reinitialisation du code de connexion d'un utilisateur. */
@Data
public class UpdateCodeRequest {
    private String code;
}
