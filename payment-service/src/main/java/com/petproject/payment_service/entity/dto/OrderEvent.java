package com.petproject.payment_service.entity.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderEvent {
    private String eventId;
    private Long id;
    private Long userId;
    private BigDecimal totalAmount;
}
