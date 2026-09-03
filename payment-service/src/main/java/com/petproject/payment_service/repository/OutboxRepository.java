package com.petproject.payment_service.repository;

import com.petproject.payment_service.entity.dto.OutboxMessage;
import org.springframework.stereotype.Repository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class OutboxRepository {

    public void save(Connection conn, String eventType, String payload) throws SQLException {
        String sql = "INSERT INTO outbox_messages (id, event_type, payload, created_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.randomUUID());
            stmt.setString(2, eventType);
            stmt.setString(3, payload);
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
        }
    }

    public List<OutboxMessage> getPendingMessages(Connection conn, int limit) throws SQLException {
        String sql = "SELECT id, event_type, payload FROM outbox_messages ORDER BY created_at ASC LIMIT ?";
        List<OutboxMessage> messages = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(OutboxMessage.builder()
                            .id((UUID) rs.getObject("id"))
                            .eventType(rs.getString("event_type"))
                            .payload(rs.getString("payload"))
                            .build());
                }
            }
        }
        return messages;
    }

    public void deleteMessage(Connection conn, UUID messageId) throws SQLException {
        String sql = "DELETE FROM outbox_messages WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, messageId);
            stmt.executeUpdate();
        }
    }
}