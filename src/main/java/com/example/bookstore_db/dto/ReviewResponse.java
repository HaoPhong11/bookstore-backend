package com.example.bookstore_db.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private String userName;   // Chỉ gửi tên, không gửi cả Object User
    private int rating;
    private String comment;
    private String createdAt;
}
