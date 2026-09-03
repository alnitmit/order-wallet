package com.petproject.payment_service.repository;

import com.petproject.payment_service.entity.Wallet;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class WalletRepository {

    public Optional<Wallet> findByUserId(Connection conn, Long userId) throws SQLException {
        String sql = "SELECT user_id, balance FROM wallets WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(Wallet.builder()
                            .userId(rs.getLong("user_id"))
                            .balance(rs.getBigDecimal("balance"))
                            .build());
                }
            }
        }
        return Optional.empty();
    }

    public void update(Connection conn, Wallet wallet) throws SQLException {
        String sql = "UPDATE wallets SET balance = ? WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, wallet.getBalance());
            stmt.setLong(2, wallet.getUserId());
            stmt.executeUpdate();
        }
    }

    public void insert(Connection conn, Wallet wallet) throws SQLException {
        String sql = "INSERT INTO wallets (user_id, balance) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, wallet.getUserId());
            stmt.setBigDecimal(2, wallet.getBalance());
            stmt.executeUpdate();
        }
    }
}