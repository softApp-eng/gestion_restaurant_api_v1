package com.hotel.pos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroTicket;

    @Column(nullable = false)
    private LocalDateTime dateHeure;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;

    private String numeroChambre; // rempli seulement si paymentType = CHAMBRE

    @Column(nullable = false)
    private BigDecimal montantTotal;

    @Column(nullable = false)
    private boolean annule = false;

    @Column(nullable = false)
    private boolean cloture = false;

    private String caissier;

    @ManyToOne
    @JoinColumn(name = "cash_register_closing_id")
    private CashRegisterClosing clotureRef;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> items = new ArrayList<>();
}
