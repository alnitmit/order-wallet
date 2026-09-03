package com.petproject.order_service.repository;

import com.petproject.order_service.entity.InboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InboxRepository extends JpaRepository<InboxMessage, String> {
}