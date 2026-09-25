package com.hotel.pos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class DailyReportResponse {
    private LocalDate date;
    private BigDecimal totalEspece;
    private BigDecimal totalChambre;
    private BigDecimal totalGeneral;
    private int nombreTickets;
    private List<SaleResponse> ventes;
}
