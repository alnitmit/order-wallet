package com.petproject.order_service.сontroller;

import com.petproject.order_service.entity.dto.OrderCreateRequest;
import com.petproject.order_service.entity.dto.OrderResponse;
import com.petproject.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Long createOrder(
            @RequestHeader("x-user-id") Long userId,
            @Valid @RequestBody OrderCreateRequest request) {

        return orderService.createOrder(userId, request);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }
}
