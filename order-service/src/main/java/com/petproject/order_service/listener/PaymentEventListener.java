package com.petproject.order_service.listener;

import com.petproject.order_service.entity.InboxMessage;
import com.petproject.order_service.entity.Order;
import com.petproject.order_service.repository.InboxRepository;
import com.petproject.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final ObjectMapper objectMapper;
    private final InboxRepository inboxRepository;
    private final OrderRepository orderRepository;

    @KafkaListener(topics = "payment-events", groupId = "order-group")
    @Transactional
    public void handlePaymentEvent(String message) {
        try {
            Map<String, Object> payload = objectMapper.readValue(message, new TypeReference<>() {});
            String eventId = (String) payload.get("eventId");
            Long orderId = ((Number) payload.get("orderId")).longValue();
            String status = (String) payload.get("status");

            if (inboxRepository.existsById(eventId)) {
                log.info("Событие {} уже обработано, пропускаем (Inbox)", eventId);
                return;
            }

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Заказ не найден: " + orderId));

            if ("PAYMENT_SUCCESS".equals(status)) {
                order.setStatus("PAID");
            } else {
                order.setStatus("CANCELED");
            }
            orderRepository.save(order);

            inboxRepository.save(InboxMessage.builder().eventId(eventId).build());

            log.info("Статус заказа {} обновлен на {}", orderId, order.getStatus());

        } catch (RuntimeException e) {
            log.error("Ошибка парсинга JSON из Kafka: {}", message, e);
        }
    }
}