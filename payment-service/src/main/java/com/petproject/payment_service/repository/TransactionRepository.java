package com.petproject.payment_service.repository;

import com.petproject.payment_service.entity.Transaction;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

@Repository
public class TransactionRepository {

    public void save(Connection conn, Transaction tx) throws SQLException {
        String sql = "INSERT INTO transactions (id, order_id, user_id, amount, status, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, tx.getId());
            stmt.setLong(2, tx.getOrderId());
            stmt.setLong(3, tx.getUserId());
            stmt.setBigDecimal(4, tx.getAmount());
            stmt.setString(5, tx.getStatus());
            stmt.setTimestamp(6, Timestamp.valueOf(tx.getCreatedAt()));
            stmt.executeUpdate();
        }
    }
}