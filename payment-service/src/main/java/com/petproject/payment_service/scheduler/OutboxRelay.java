package com.petproject.payment_service.scheduler;

import com.petproject.payment_service.entity.dto.OutboxMessage;
import com.petproject.payment_service.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelay {

    private final DataSource dataSource;
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)
    public void processOutbox() {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try {
                List<OutboxMessage> messages = outboxRepository.getPendingMessages(conn, 50);

                for (OutboxMessage message : messages) {
                    kafkaTemplate.send("payment-events", message.getPayload()).get();

                    outboxRepository.deleteMessage(conn, message.getId());
                    log.info("Событие из Outbox отправлено: {}", message.getId());
                }

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                log.error("Ошибка при отправке сообщений из Outbox. Транзакция откатана.", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            log.error("Ошибка подключения к БД в планировщике", e);
        }
    }
}