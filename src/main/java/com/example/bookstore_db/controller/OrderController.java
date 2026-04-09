package com.example.bookstore_db.controller;

import com.example.bookstore_db.dto.OrderRequest;
import com.example.bookstore_db.dto.PaymentUrlResponse;
import com.example.bookstore_db.entity.Order;
import com.example.bookstore_db.repository.OrderRepository;
import com.example.bookstore_db.service.MoMoService;
import com.example.bookstore_db.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired private OrderService orderService;
    @Autowired private OrderRepository orderRepository;
    @Autowired private MoMoService moMoService;

    /** Tạo đơn hàng. Trả về PaymentUrlResponse (payUrl=null nếu COD). */
    @PostMapping
    public ResponseEntity<PaymentUrlResponse> placeOrder(@RequestBody OrderRequest dto) {
        try {
            Order order = orderService.createOrder(dto);

            if ("MOMO".equalsIgnoreCase(dto.getPaymentMethod())) {
                MoMoService.MoMoResult result = moMoService.createPayment(order.getId(), order.getTotalPrice());
                return ResponseEntity.ok(
                        new PaymentUrlResponse(order.getId(), result.payUrl, result.deeplink, "MOMO"));
            } else {
                // COD — không cần payment URL
                return ResponseEntity.ok(
                        new PaymentUrlResponse(order.getId(), null, null, dto.getPaymentMethod()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /** Lấy lịch sử đơn hàng của 1 user. */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(orderRepository.findByUserIdOrderByCreatedAtDesc(userId));
    }

    /** Hủy đơn hàng PENDING. */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        Optional<Order> opt = orderRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Order order = opt.get();
        if (!"PENDING".equals(order.getStatus())) return ResponseEntity.badRequest().build();
        order.setStatus("CANCELLED");
        orderRepository.save(order);
        return ResponseEntity.ok().build();
    }

    /** Lấy lại payUrl MoMo cho đơn PENDING (dùng khi user muốn thanh toán lại). */
    @GetMapping("/{id}/repay")
    public ResponseEntity<PaymentUrlResponse> repayOrder(@PathVariable Long id) {
        Optional<Order> opt = orderRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Order order = opt.get();
        if (!"PENDING".equals(order.getStatus())) return ResponseEntity.badRequest().build();
        try {
            MoMoService.MoMoResult result = moMoService.createPayment(id, order.getTotalPrice());
            return ResponseEntity.ok(new PaymentUrlResponse(id, result.payUrl, result.deeplink, "MOMO"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}