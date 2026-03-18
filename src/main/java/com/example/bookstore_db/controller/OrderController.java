package com.example.bookstore_db.controller;

import com.example.bookstore_db.dto.OrderRequest;
import com.example.bookstore_db.entity.Order;
import com.example.bookstore_db.repository.OrderRepository;
import com.example.bookstore_db.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    // API Đặt Hàng
    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody OrderRequest orderRequestDTO) {
        try {
            Order newOrder = orderService.createOrder(orderRequestDTO);
            return ResponseEntity.ok(newOrder); // Trả về đơn hàng vừa tạo thành công
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build(); // Báo lỗi 400
        }
    }

    // API Lấy lịch sử đơn hàng của 1 user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return ResponseEntity.ok(orders);
    }
}