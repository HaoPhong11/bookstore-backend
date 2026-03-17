package com.example.bookstore_db.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    private String googleBookId; // ID từ API
    private String bookTitle;    // Tên sách lúc mua
    private String thumbnailUrl; // Ảnh bìa lúc mua
    private Double priceAtPurchase; // Giá lúc mua
    private Integer quantity;    // Số lượng
}