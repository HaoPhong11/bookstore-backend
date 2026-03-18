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

    @Column(name = "book_id")
    private String bookId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private LocalDateTime createdAt = LocalDateTime.now();

    // lấy tên User
    public String getUserName() {
        return user != null ? user.getFullName() : "Ẩn danh";
    }

}