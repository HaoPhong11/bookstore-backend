package com.example.bookstore_db.service;

import com.example.bookstore_db.dto.OrderRequest;
import com.example.bookstore_db.entity.Order;
import com.example.bookstore_db.entity.OrderItem;
import com.example.bookstore_db.entity.User;
import com.example.bookstore_db.repository.OrderRepository;
import com.example.bookstore_db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private EmailService emailService;

    @Transactional
    public Order createOrder(OrderRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Tạo đối tượng Order
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(request.getTotalAmount());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");

        // 2. Chuyển đổi từ DTO sang Entity OrderItem
        List<OrderItem> orderItems = request.getItems().stream().map(itemDto -> {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setGoogleBookId(itemDto.getGoogleBookId());
            item.setBookTitle(itemDto.getTitle());
            item.setThumbnailUrl(itemDto.getThumbnail());
            item.setPriceAtPurchase(itemDto.getPrice());
            item.setQuantity(itemDto.getQuantity());
            return item;
        }).toList();

        order.setOrderItems(orderItems);

        // 3. Lưu vào DB (Lưu Order sẽ tự lưu luôn OrderItems nhờ CascadeType.ALL)
        Order savedOrder = orderRepository.save(order);

        // 4. Gửi Mail xác nhận (chạy ngầm Async)
        emailService.sendOrderConfirmation(user.getEmail(), user.getFullName(), savedOrder.getId(), savedOrder.getTotalAmount());

        return savedOrder;
    }
}