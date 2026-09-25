package com.hotel.pos.service;

import com.hotel.pos.dto.DailyReportResponse;
import com.hotel.pos.dto.SaleResponse;
import com.hotel.pos.model.PaymentType;
import com.hotel.pos.model.Sale;
import com.hotel.pos.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final SaleRepository saleRepository;

    public ReportService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public DailyReportResponse getDailyReport(LocalDate date) {
        LocalDateTime debut = date.atStartOfDay();
        LocalDateTime fin = date.plusDays(1).atStartOfDay();
        List<Sale> ventes = saleRepository.findByDateHeureBetweenAndAnnuleFalse(debut, fin);

        BigDecimal totalEspece = sum(ventes, PaymentType.ESPECE);
        BigDecimal totalChambre = sum(ventes, PaymentType.CHAMBRE);
        BigDecimal totalGeneral = totalEspece.add(totalChambre);

        List<SaleResponse> reponses = ventes.stream().map(this::toResponse).collect(Collectors.toList());

        return new DailyReportResponse(date, totalEspece, totalChambre, totalGeneral, ventes.size(), reponses);
    }

    private BigDecimal sum(List<Sale> ventes, PaymentType type) {
        return ventes.stream()
                .filter(v -> v.getPaymentType() == type)
                .map(Sale::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private SaleResponse toResponse(Sale sale) {
        var items = sale.getItems().stream()
                .map(i -> new com.hotel.pos.dto.SaleItemResponse(i.getNomProduit(), i.getPrixUnitaire(), i.getQuantite(), i.getSousTotal()))
                .collect(Collectors.toList());
        return new SaleResponse(sale.getId(), sale.getNumeroTicket(), sale.getDateHeure(), sale.getPaymentType(),
                sale.getNumeroChambre(), sale.getMontantTotal(), sale.getCaissier(), sale.isAnnule(), items);
    }
}
