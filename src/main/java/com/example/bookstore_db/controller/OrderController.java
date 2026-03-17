package com.example.bookstore_db.controller;

import com.example.bookstore_db.dto.OrderRequest;
import com.example.bookstore_db.entity.Order;
import com.example.bookstore_db.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody OrderRequest request) {
        // Lấy username từ Token đã xác thực
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            Order order = orderService.createOrder(request, username);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi đặt hàng: " + e.getMessage());
        }
    }
}