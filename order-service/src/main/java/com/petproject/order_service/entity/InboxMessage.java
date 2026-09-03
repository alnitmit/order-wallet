package com.petproject.order_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "inbox_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InboxMessage {

    @Id
    @Column(name = "event_id")
    private String eventId;

    @Column(name = "processed_at", insertable = false, updatable = false)
    private LocalDateTime processedAt;
}