package com.hotel.pos.dto;

import com.hotel.pos.model.PaymentType;
import lombok.Data;
import java.util.List;

@Data
public class SaleRequest {
    private PaymentType paymentType;
    private String numeroChambre; // requis si paymentType = CHAMBRE
    private List<SaleItemRequest> items;
}
