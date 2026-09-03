package com.petproject.order_service.scheduler;

import com.petproject.order_service.entity.OutboxMessage;
import com.petproject.order_service.repository.OutboxRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelayString = "5000")
    @Transactional
    public void processOutboxMessages() {

        List<OutboxMessage> pendingMessages = outboxRepository.findByStatusOrderByCreatedAtAsc("PENDING");

        if (pendingMessages.isEmpty()) {
            return;
        }

        log.info("Найдено {} сообщений для отправки в Kafka", pendingMessages.size());

        for (OutboxMessage message : pendingMessages) {
            try {
                kafkaTemplate.send("order-events", message.getId().toString(), message.getPayload()).get();

                message.setStatus("PROCESSED");
                log.info("Сообщение {} успешно отправлено", message.getId());

            } catch (Exception e) {
                log.error("Ошибка отправки сообщения {} в Kafka", message.getId(), e);
            }
        }

        outboxRepository.saveAll(pendingMessages);
    }
}
