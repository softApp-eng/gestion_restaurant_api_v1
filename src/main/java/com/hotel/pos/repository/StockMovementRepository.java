package com.hotel.pos.repository;

import com.hotel.pos.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findTop100ByOrderByDateHeureDesc();

    List<StockMovement> findTop100ByProductIdOrderByDateHeureDesc(Long productId);
}
