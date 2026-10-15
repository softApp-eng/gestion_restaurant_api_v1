package com.hotel.pos.dto;

import com.hotel.pos.model.Role;
import lombok.Data;

/** Creation d'un compte (caissier par defaut). */
@Data
public class CreateUserRequest {
    private String username;
    private String code;   // code de connexion (mot de passe)
    private Role role;     // optionnel, CAISSIER par defaut
}
