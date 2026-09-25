package com.hotel.pos.service;

import com.hotel.pos.dto.ClosingResponse;
import com.hotel.pos.model.CashRegisterClosing;
import com.hotel.pos.model.PaymentType;
import com.hotel.pos.model.Sale;
import com.hotel.pos.repository.CashRegisterClosingRepository;
import com.hotel.pos.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Cloture de caisse : prend toutes les ventes non annulees et non deja cloturees,
 * calcule les totaux par mode de paiement, cree un enregistrement de cloture,
 * et marque les ventes comme cloturees. Utilise la date systeme (LocalDate.now()).
 */
@Service
public class CashRegisterService {

    private final SaleRepository saleRepository;
    private final CashRegisterClosingRepository closingRepository;

    public CashRegisterService(SaleRepository saleRepository, CashRegisterClosingRepository closingRepository) {
        this.saleRepository = saleRepository;
        this.closingRepository = closingRepository;
    }

    @Transactional
    public ClosingResponse closeRegister(boolean automatique) {
        List<Sale> ventes = saleRepository.findByClotureFalseAndAnnuleFalse();

        BigDecimal totalEspece = sum(ventes, PaymentType.ESPECE);
        BigDecimal totalChambre = sum(ventes, PaymentType.CHAMBRE);
        BigDecimal totalGeneral = totalEspece.add(totalChambre);

        CashRegisterClosing closing = new CashRegisterClosing();
        closing.setDateCloture(LocalDate.now()); // date systeme
        closing.setHeureCloture(LocalDateTime.now());
        closing.setTotalEspece(totalEspece);
        closing.setTotalChambre(totalChambre);
        closing.setTotalGeneral(totalGeneral);
        closing.setNombreTickets(ventes.size());
        closing.setAutomatique(automatique);
        CashRegisterClosing saved = closingRepository.save(closing);

        for (Sale v : ventes) {
            v.setCloture(true);
            v.setClotureRef(saved);
        }
        saleRepository.saveAll(ventes);

        return toResponse(saved);
    }

    public List<ClosingResponse> getHistorique() {
        return closingRepository.findAllByOrderByHeureClotureDesc()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private BigDecimal sum(List<Sale> ventes, PaymentType type) {
        return ventes.stream()
                .filter(v -> v.getPaymentType() == type)
                .map(Sale::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private ClosingResponse toResponse(CashRegisterClosing c) {
        return new ClosingResponse(c.getId(), c.getDateCloture(), c.getHeureCloture(),
                c.getTotalEspece(), c.getTotalChambre(), c.getTotalGeneral(), c.getNombreTickets(), c.isAutomatique());
    }
}
