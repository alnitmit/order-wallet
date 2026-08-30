package com.petproject.order_service.entity.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private String status;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;
}
