package com.petproject.payment_service.listener;

import com.petproject.payment_service.entity.dto.OrderEvent;
import com.petproject.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;

    @KafkaListener(topics = "order-events", groupId = "payment-group")
    public void consumeOrderEvent(String message) {
        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
            paymentService.processPayment(event);
        } catch (Exception e) {
            log.error("Ошибка при обработке сообщения из Kafka: {}", message, e);
        }
    }
}