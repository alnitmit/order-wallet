package com.petproject.payment_service.service;

import com.petproject.payment_service.entity.dto.*;
import com.petproject.payment_service.entity.Transaction;
import com.petproject.payment_service.entity.Wallet;
import com.petproject.payment_service.repository.InboxRepository;
import com.petproject.payment_service.repository.OutboxRepository;
import com.petproject.payment_service.repository.TransactionRepository;
import com.petproject.payment_service.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final DataSource dataSource;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final InboxRepository inboxRepository;
    private final OutboxRepository outboxRepository;



    public void processPayment(OrderEvent event) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try {
                if (inboxRepository.isProcessed(conn, event.getEventId())) {
                    log.info("Сообщение {} уже обработано, пропускаем", event.getEventId());
                    conn.commit();
                    return;
                }

                Wallet wallet = walletRepository.findByUserId(conn, event.getUserId())
                        .orElseGet(() -> {
                            Wallet newWallet = new Wallet(event.getUserId(), BigDecimal.ZERO);
                            try {
                                walletRepository.insert(conn, newWallet);
                            } catch (SQLException e) {
                                throw new RuntimeException("Ошибка при создании кошелька пользователя", e);
                            }
                            return newWallet;
                        });

                String paymentStatus;
                if (wallet.getBalance().compareTo(event.getTotalAmount()) < 0) {
                    paymentStatus = "PAYMENT_FAILED";
                    log.error("Недостаточно средств у пользователя {}", event.getUserId());
                } else {
                    paymentStatus = "PAYMENT_SUCCESS";
                    wallet.setBalance(wallet.getBalance().subtract(event.getTotalAmount()));
                    walletRepository.update(conn, wallet);
                }

                saveTransaction(conn, event, paymentStatus);

                inboxRepository.save(conn, event.getEventId());

                String payload = String.format(
                        "{\"eventId\":\"%s\", \"orderId\":%d, \"status\":\"%s\"}",
                        UUID.randomUUID().toString(), event.getId(), paymentStatus
                );
                outboxRepository.save(conn, "PAYMENT_RESULT", payload);

                conn.commit();
                log.info("Транзакция для заказа {} успешно завершена", event.getId());

            } catch (Exception e) {
                conn.rollback();
                log.error("Ошибка транзакции, откат изменений: {}", e.getMessage());
                throw new RuntimeException("Ошибка проведения платежа", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            log.error("Ошибка подключения к БД", e);
        }
    }

    private void saveTransaction(Connection conn, OrderEvent event, String status) throws SQLException {
        transactionRepository.save(conn, Transaction.builder()
                .id(UUID.randomUUID())
                .orderId(event.getId())
                .userId(event.getUserId())
                .amount(event.getTotalAmount())
                .status(status)
                .createdAt(LocalDateTime.now())
                .build());
    }
}