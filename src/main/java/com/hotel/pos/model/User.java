package com.hotel.pos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; // hash BCrypt

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /** Un compte desactive ne peut plus se connecter. */
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean actif = true;

    private LocalDateTime dateCreation;
}
