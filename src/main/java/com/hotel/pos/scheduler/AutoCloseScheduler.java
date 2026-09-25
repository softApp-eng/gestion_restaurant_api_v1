package com.hotel.pos.scheduler;

import com.hotel.pos.service.CashRegisterService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Cloture automatique de la caisse chaque jour a l'heure configuree (par defaut 23:59:00),
 * en utilisant la date systeme du serveur.
 */
@Component
public class AutoCloseScheduler {

    private final CashRegisterService cashRegisterService;

    public AutoCloseScheduler(CashRegisterService cashRegisterService) {
        this.cashRegisterService = cashRegisterService;
    }

    @Scheduled(cron = "${app.cloture.cron:0 59 23 * * *}")
    public void clotureAutomatique() {
        cashRegisterService.closeRegister(true);
    }
}
