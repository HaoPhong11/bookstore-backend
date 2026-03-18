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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/{bookId}")
    public ResponseEntity<?> getReviews(@PathVariable String bookId) {
        Map<String, Object> response = new HashMap<>();
        response.put("averageRating", reviewService.getAverageRating(bookId));
        response.put("reviews", reviewService.getReviewsByBook(bookId));
        return ResponseEntity.ok(response);
    }

//    // Khách hàng gửi 1 đánh giá mới
//    @PostMapping
//    public Review addReview(@RequestBody Review review) {
//        return reviewRepository.save(review);
//    }

    // Lấy tất cả đánh giá của 1 cuốn sách
    @GetMapping("/book/{bookId}")
    public List<Review> getReviewsByBookId(@PathVariable String bookId) {
        return reviewRepository.findByBookId(bookId);
    }

    // Sửa đánh giá
    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable Long id, @RequestBody Review reviewDetails) {
        return reviewRepository.findById(id).map(review -> {
            review.setRating(reviewDetails.getRating());
            review.setComment(reviewDetails.getComment());
            Review updatedReview = reviewRepository.save(review);
            return ResponseEntity.ok(updatedReview);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> addReview(@RequestBody ReviewRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Gọi Service xử lý
        reviewService.saveReview(request, username);

        return ResponseEntity.ok("Đã đăng đánh giá thành công!");
    }

    // API lấy điểm đánh giá trung bình của 1 cuốn sách
    @GetMapping("/book/{bookId}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable String bookId) {
        Double avgRating = reviewRepository.getAverageRatingByBookId(bookId);
        return ResponseEntity.ok(avgRating != null ? avgRating : 0.0);
    }

}