package com.petproject.order_service.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {

    @NotBlank(message = "Название товара не должно быть пустым")
    private String productName;

    @NotNull(message = "Цена обязательна")
    @Positive(message = "Цена должна быть больше нуля")
    private BigDecimal price;

    @NotNull(message = "Количество обязательно")
    @Positive(message = "Количество должно быть больше нуля")
    private Integer quantity;
}
