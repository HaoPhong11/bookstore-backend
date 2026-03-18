package com.example.bookstore_db.dto;
import lombok.Data;

@Data
public class ReviewRequest {
    private String bookId;
    private int rating; // từ 1 đến 5
    private String comment;


}
