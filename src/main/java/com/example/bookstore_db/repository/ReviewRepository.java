package com.example.bookstore_db.repository;

import com.example.bookstore_db.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByGoogleBookIdOrderByCreatedAtDesc(String googleBookId);
}
