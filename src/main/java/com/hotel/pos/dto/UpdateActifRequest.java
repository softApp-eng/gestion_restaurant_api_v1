package com.hotel.pos.dto;

import lombok.Data;

/** Activation / desactivation d'un compte. */
@Data
public class UpdateActifRequest {
    private boolean actif;
}
