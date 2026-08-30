package com.petproject.order_service.repository;

import com.petproject.order_service.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @NonNull
    @EntityGraph(attributePaths = {"items"})
    Optional<Order> findById(@NonNull Long id);
}
