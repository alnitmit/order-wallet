package com.petproject.payment_service.entity.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class OutboxMessage {
    private UUID id;
    private String eventType;
    private String payload;
}