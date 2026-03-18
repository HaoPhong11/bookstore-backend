package com.example.bookstore_db.service;

import com.example.bookstore_db.dto.ReviewRequest;
import com.example.bookstore_db.dto.ReviewResponse;
import com.example.bookstore_db.entity.Review;
import com.example.bookstore_db.entity.User;
import com.example.bookstore_db.repository.ReviewRepository;
import com.example.bookstore_db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private UserRepository userRepository; // Bơm vào đây mới chuẩn


    public Double getAverageRating(String bookId) {
        List<Review> reviews = reviewRepository.findByBookIdOrderByCreatedAtDesc(bookId);
        if (reviews.isEmpty()) return 0.0;
        double sum = reviews.stream().mapToInt(Review::getRating).sum();
        return sum / reviews.size();
    }

    // Hàm lưu Review mới
    public void saveReview(ReviewRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Review review = new Review();
        review.setBookId(request.getBookId());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setUser(user);
        review.setCreatedAt(LocalDateTime.now());

        reviewRepository.save(review);
    }

    public List<ReviewResponse> getReviewsByBook(String bookId) {
        List<Review> reviews = reviewRepository.findByBookIdOrderByCreatedAtDesc(bookId);

        return reviews.stream().map(r -> new ReviewResponse(
                r.getId(),
                r.getUser().getFullName(), // Lấy tên người đánh giá ở đây
                r.getRating(),
                r.getComment(),
                r.getCreatedAt().toString()
        )).toList();
    }
}
