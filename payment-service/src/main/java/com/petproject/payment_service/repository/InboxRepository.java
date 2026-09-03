package com.petproject.payment_service.repository;

import org.springframework.stereotype.Repository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class InboxRepository {

    public boolean isProcessed(Connection conn, String eventId) throws SQLException {
        String sql = "SELECT 1 FROM inbox_messages WHERE event_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void save(Connection conn, String eventId) throws SQLException {
        String sql = "INSERT INTO inbox_messages (event_id) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventId);
            stmt.executeUpdate();
        }
    }
}