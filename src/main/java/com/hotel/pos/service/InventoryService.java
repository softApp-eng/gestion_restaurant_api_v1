package com.hotel.pos.service;

import com.hotel.pos.dto.InventoryItemDto;
import com.hotel.pos.dto.StockMovementDto;
import com.hotel.pos.model.MovementType;
import com.hotel.pos.model.Product;
import com.hotel.pos.model.StockMovement;
import com.hotel.pos.repository.ProductRepository;
import com.hotel.pos.repository.StockMovementRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gestion de l'inventaire : niveaux de stock, seuils d'alerte et historique des mouvements.
 */
@Service
public class InventoryService {

    private final ProductRepository productRepository;
    private final StockMovementRepository movementRepository;

    public InventoryService(ProductRepository productRepository, StockMovementRepository movementRepository) {
        this.productRepository = productRepository;
        this.movementRepository = movementRepository;
    }

    // ------------------------------------------------------------------
    // Consultation
    // ------------------------------------------------------------------

    public List<InventoryItemDto> getAll() {
        return productRepository.findAll().stream()
                .sorted(Comparator.comparing(Product::getNom, String.CASE_INSENSITIVE_ORDER))
                .map(this::toItem)
                .collect(Collectors.toList());
    }

    public List<StockMovementDto> movements(int limit) {
        int max = limit <= 0 ? 100 : Math.min(limit, 500);
        return movementRepository.findTop100ByOrderByDateHeureDesc().stream()
                .limit(max)
                .map(this::toMovement)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------
    // Operations manuelles (admin)
    // ------------------------------------------------------------------

    @Transactional
    public InventoryItemDto entree(Long productId, int quantite, String motif, String utilisateur) {
        if (quantite <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La quantite doit etre positive");
        }
        Product p = find(productId);
        p.setStock(p.getStock() + quantite);
        productRepository.save(p);
        record(p, MovementType.ENTREE, quantite, motif, utilisateur);
        return toItem(p);
    }

    @Transactional
    public InventoryItemDto sortie(Long productId, int quantite, String motif, String utilisateur) {
        if (quantite <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La quantite doit etre positive");
        }
        Product p = find(productId);
        int nouvelle = Math.max(0, p.getStock() - quantite);
        int delta = nouvelle - p.getStock();
        p.setStock(nouvelle);
        productRepository.save(p);
        record(p, MovementType.SORTIE, delta, motif, utilisateur);
        return toItem(p);
    }

    @Transactional
    public InventoryItemDto ajuster(Long productId, int nouvelleValeur, String motif, String utilisateur) {
        if (nouvelleValeur < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le stock ne peut pas etre negatif");
        }
        Product p = find(productId);
        int delta = nouvelleValeur - p.getStock();
        p.setStock(nouvelleValeur);
        productRepository.save(p);
        record(p, MovementType.AJUSTEMENT, delta, motif, utilisateur);
        return toItem(p);
    }

    @Transactional
    public InventoryItemDto setSeuil(Long productId, int seuilAlerte) {
        if (seuilAlerte < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le seuil doit etre positif");
        }
        Product p = find(productId);
        p.setSeuilAlerte(seuilAlerte);
        return toItem(productRepository.save(p));
    }

    // ------------------------------------------------------------------
    // Integrations (ventes / annulations)
    // ------------------------------------------------------------------

    /** Decremente le stock d'un produit suite a une vente (sans passer sous 0). */
    @Transactional
    public void appliquerVente(Product p, int quantite, String motif, String utilisateur) {
        int nouvelle = Math.max(0, p.getStock() - quantite);
        int delta = nouvelle - p.getStock();
        if (delta == 0) return;
        p.setStock(nouvelle);
        productRepository.save(p);
        record(p, MovementType.SORTIE, delta, motif, utilisateur);
    }

    /** Reincremente le stock d'un produit lors de l'annulation d'une vente. */
    @Transactional
    public void appliquerAnnulation(Product p, int quantite, String motif, String utilisateur) {
        if (quantite <= 0) return;
        p.setStock(p.getStock() + quantite);
        productRepository.save(p);
        record(p, MovementType.ENTREE, quantite, motif, utilisateur);
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private Product find(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produit introuvable"));
    }

    private void record(Product p, MovementType type, int delta, String motif, String utilisateur) {
        String m = (motif == null || motif.isBlank()) ? motifParDefaut(type) : motif.trim();
        movementRepository.save(new StockMovement(
                null, p.getId(), p.getNom(), type, delta, p.getStock(), m, LocalDateTime.now(), utilisateur));
    }

    private String motifParDefaut(MovementType type) {
        switch (type) {
            case ENTREE: return "Approvisionnement";
            case SORTIE: return "Consommation";
            default: return "Ajustement d'inventaire";
        }
    }

    private InventoryItemDto toItem(Product p) {
        return new InventoryItemDto(
                p.getId(), p.getNom(), p.getCategorie(), p.getPrix(), p.isActif(),
                p.getStock(), p.getSeuilAlerte(),
                p.getStock() <= p.getSeuilAlerte(),
                p.getStock() <= 0);
    }

    private StockMovementDto toMovement(StockMovement m) {
        return new StockMovementDto(m.getId(), m.getProductId(), m.getNomProduit(), m.getType(),
                m.getQuantite(), m.getStockApres(), m.getMotif(), m.getDateHeure(), m.getUtilisateur());
    }
}
