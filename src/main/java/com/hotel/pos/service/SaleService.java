package com.hotel.pos.service;

import com.hotel.pos.dto.*;
import com.hotel.pos.model.*;
import com.hotel.pos.repository.ProductRepository;
import com.hotel.pos.repository.SaleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    public SaleService(SaleRepository saleRepository, ProductRepository productRepository,
                       InventoryService inventoryService) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
    }

    /** Ligne de vente memorisee pour appliquer la sortie de stock. */
    private record VenteLigne(Product product, int quantite) {}

    @Transactional
    public SaleResponse createSale(SaleRequest req, String caissier) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le ticket est vide");
        }
        if (req.getPaymentType() == PaymentType.CHAMBRE &&
                (req.getNumeroChambre() == null || req.getNumeroChambre().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Numero de chambre requis");
        }

        Sale sale = new Sale();
        sale.setDateHeure(LocalDateTime.now());
        sale.setPaymentType(req.getPaymentType());
        sale.setNumeroChambre(req.getPaymentType() == PaymentType.CHAMBRE ? req.getNumeroChambre() : null);
        sale.setCaissier(caissier);
        sale.setNumeroTicket(genererNumeroTicket());

        List<SaleItem> items = new ArrayList<>();
        List<VenteLigne> lignes = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (SaleItemRequest ir : req.getItems()) {
            if (ir.getQuantite() <= 0) continue;
            Product product = productRepository.findById(ir.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produit introuvable"));
            if (!product.isActif()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produit indisponible: " + product.getNom());
            }
            SaleItem item = new SaleItem();
            item.setSale(sale);
            item.setProductId(product.getId());
            item.setNomProduit(product.getNom());
            item.setPrixUnitaire(product.getPrix());
            item.setQuantite(ir.getQuantite());
            BigDecimal sousTotal = product.getPrix().multiply(BigDecimal.valueOf(ir.getQuantite()));
            item.setSousTotal(sousTotal);
            items.add(item);
            lignes.add(new VenteLigne(product, ir.getQuantite()));
            total = total.add(sousTotal);
        }
        if (items.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le ticket est vide");
        }
        sale.setItems(items);
        sale.setMontantTotal(total);

        Sale saved = saleRepository.save(sale);

        // Sortie de stock pour chaque article vendu
        String motif = "Vente " + saved.getNumeroTicket();
        for (VenteLigne l : lignes) {
            inventoryService.appliquerVente(l.product(), l.quantite(), motif, caissier);
        }

        return toResponse(saved);
    }

    @Transactional
    public SaleResponse annulerVente(Long id, String utilisateur) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vente introuvable"));
        if (sale.isCloture()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Impossible d'annuler : caisse deja cloturee pour ce ticket");
        }
        if (sale.isAnnule()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette vente est deja annulee");
        }
        sale.setAnnule(true);
        Sale saved = saleRepository.save(sale);

        // Retour en stock des articles annules
        String motif = "Annulation " + saved.getNumeroTicket();
        for (SaleItem it : saved.getItems()) {
            if (it.getProductId() == null) continue;
            productRepository.findById(it.getProductId())
                    .ifPresent(p -> inventoryService.appliquerAnnulation(p, it.getQuantite(), motif, utilisateur));
        }

        return toResponse(saved);
    }

    public List<SaleResponse> getVentesEntre(LocalDateTime debut, LocalDateTime fin) {
        return saleRepository.findByDateHeureBetweenAndAnnuleFalse(debut, fin)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public SaleResponse getById(Long id) {
        return toResponse(saleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vente introuvable")));
    }

    private String genererNumeroTicket() {
        LocalDate today = LocalDate.now();
        LocalDateTime debut = today.atStartOfDay();
        LocalDateTime fin = today.plusDays(1).atStartOfDay();
        long count = saleRepository.countByDateHeureBetween(debut, fin) + 1;
        String datePart = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "T" + datePart + "-" + String.format("%04d", count);
    }

    private SaleResponse toResponse(Sale sale) {
        List<SaleItemResponse> items = sale.getItems().stream()
                .map(i -> new SaleItemResponse(i.getNomProduit(), i.getPrixUnitaire(), i.getQuantite(), i.getSousTotal()))
                .collect(Collectors.toList());
        return new SaleResponse(sale.getId(), sale.getNumeroTicket(), sale.getDateHeure(), sale.getPaymentType(),
                sale.getNumeroChambre(), sale.getMontantTotal(), sale.getCaissier(), sale.isAnnule(), items);
    }
}
