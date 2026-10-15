package com.hotel.pos.dto;

import com.hotel.pos.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Compte utilisateur expose a l'administration (sans le mot de passe). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private Role role;
    private boolean actif;
    private LocalDateTime dateCreation;
}
