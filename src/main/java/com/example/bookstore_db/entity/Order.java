package com.example.bookstore_db.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Đơn hàng này của ai

    private Double totalAmount;
    private LocalDateTime orderDate;
    private String status; // PENDING, SHIPPING, DELIVERED, CANCELLED

    // Mối quan hệ 1-N với OrderItem
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OrderItem> orderItems;
}