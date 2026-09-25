package com.hotel.pos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ClosingResponse {
    private Long id;
    private LocalDate dateCloture;
    private LocalDateTime heureCloture;
    private BigDecimal totalEspece;
    private BigDecimal totalChambre;
    private BigDecimal totalGeneral;
    private int nombreTickets;
    private boolean automatique;
}
