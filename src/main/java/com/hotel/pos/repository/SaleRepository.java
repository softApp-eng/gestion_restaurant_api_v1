package com.hotel.pos.repository;

import com.hotel.pos.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByDateHeureBetweenAndAnnuleFalse(LocalDateTime debut, LocalDateTime fin);
    List<Sale> findByClotureFalseAndAnnuleFalse();
    long countByDateHeureBetween(LocalDateTime debut, LocalDateTime fin);
}
