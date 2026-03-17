package com.example.bookstore_db.controller;

import com.example.bookstore_db.dto.ReviewRequest;
import com.example.bookstore_db.entity.Review;
import com.example.bookstore_db.entity.User;
import com.example.bookstore_db.repository.ReviewRepository;
import com.example.bookstore_db.repository.UserRepository;
import com.example.bookstore_db.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/{bookId}")
    public ResponseEntity<?> getReviews(@PathVariable String bookId) {
        Map<String, Object> response = new HashMap<>();
        response.put("averageRating", reviewService.getAverageRating(bookId));
        response.put("reviews", reviewService.getReviewsByBook(bookId));
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> postReview(@RequestBody ReviewRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Gọi Service xử lý, không cần gọi Repository ở đây nữa
        reviewService.saveReview(request, username);

        return ResponseEntity.ok("Đã đăng đánh giá thành công!");
    }
}