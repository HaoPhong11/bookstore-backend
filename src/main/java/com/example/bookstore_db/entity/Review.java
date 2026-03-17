package com.example.bookstore_db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.awt.print.Book;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String googleBookId; // Để biết review này của cuốn sách nào từ API

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Biết ai là người review

    // Tạo một hàm Getter ảo để lấy tên User cho nhanh
    public String getUserName() {
        return user != null ? user.getFullName() : "Ẩn danh";
    }
    private int rating; // 1 -> 5 sao

    @Column(columnDefinition = "TEXT")
    private String comment;

    private LocalDateTime createdAt = LocalDateTime.now();
}