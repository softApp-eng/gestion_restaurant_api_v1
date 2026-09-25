package com.hotel.pos.dto;

import com.hotel.pos.model.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class SaleResponse {
    private Long id;
    private String numeroTicket;
    private LocalDateTime dateHeure;
    private PaymentType paymentType;
    private String numeroChambre;
    private BigDecimal montantTotal;
    private String caissier;
    private boolean annule;
    private List<SaleItemResponse> items;
}
