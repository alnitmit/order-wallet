package com.petproject.order_service.service;

import com.petproject.order_service.entity.Order;
import com.petproject.order_service.entity.OrderItem;
import com.petproject.order_service.entity.OutboxMessage;
import com.petproject.order_service.entity.dto.OrderCreateRequest;
import com.petproject.order_service.entity.dto.OrderItemDto;
import com.petproject.order_service.entity.dto.OrderResponse;
import com.petproject.order_service.repository.OrderRepository;
import com.petproject.order_service.repository.OutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Long createOrder (Long userId, OrderCreateRequest request) {

        log.info("Создание заказа для userId: {}", userId);

        Order order = Order.builder().status("PENDING").userId(userId).build();

        BigDecimal totalAmount = request.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        request.getItems().forEach(itemDto -> {
            OrderItem item = OrderItem.builder()
                    .productName(itemDto.getProductName())
                    .price(itemDto.getPrice())
                    .quantity(itemDto.getQuantity())
                    .build();

            order.addItem(item);
        });

        Order savedOrder = orderRepository.save(order);

        try {
            Map<String, Object> eventPayload = Map.of(
                    "orderId", savedOrder.getId(),
                    "userId", savedOrder.getUserId(),
                    "amount", totalAmount
            );

            String jsonPayload = objectMapper.writeValueAsString(eventPayload);

            OutboxMessage outboxMessage = OutboxMessage.builder()
                    .type("ORDER_CREATED")
                    .payload(jsonPayload)
                    .status("PENDING")
                    .build();

            outboxRepository.save(outboxMessage);
            log.info("Событие ORDER_CREATED добавлено в Outbox для orderId: {}", savedOrder.getId());

        } catch (Exception e) {
            log.error("Ошибка при конвертации события в JSON для orderId: {}", savedOrder.getId(), e);
            throw new RuntimeException("Ошибка формирования события", e);
        }

        return savedOrder.getId();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById (Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден"));

        List<OrderItemDto> items;
        items = order.getItems().stream()
                .map(item -> OrderItemDto.builder()
                        .productName(item.getProductName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .build())
        .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }

}
