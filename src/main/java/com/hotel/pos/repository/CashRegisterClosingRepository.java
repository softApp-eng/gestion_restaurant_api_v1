package com.hotel.pos.repository;

import com.hotel.pos.model.CashRegisterClosing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CashRegisterClosingRepository extends JpaRepository<CashRegisterClosing, Long> {
    List<CashRegisterClosing> findAllByOrderByHeureClotureDesc();
    Optional<CashRegisterClosing> findFirstByDateClotureOrderByHeureClotureDesc(LocalDate date);
}
