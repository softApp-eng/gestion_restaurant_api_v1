package com.hotel.pos.model;

/** Sens d'un mouvement de stock. */
public enum MovementType {
    ENTREE,      // approvisionnement / retour / annulation de vente
    SORTIE,      // vente / consommation
    AJUSTEMENT   // correction manuelle d'inventaire
}
