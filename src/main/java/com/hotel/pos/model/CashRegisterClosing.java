package com.hotel.pos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cash_register_closings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashRegisterClosing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dateCloture; // date systeme du jour cloture

    @Column(nullable = false)
    private LocalDateTime heureCloture;

    @Column(nullable = false)
    private BigDecimal totalEspece = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal totalChambre = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal totalGeneral = BigDecimal.ZERO;

    @Column(nullable = false)
    private int nombreTickets;

    @Column(nullable = false)
    private boolean automatique;
}
