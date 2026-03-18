package com.example.bookstore_db.repository;

import com.example.bookstore_db.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBookIdOrderByCreatedAtDesc(String bookId);
    List<Review> findByUserId(Long userId);
    List<Review> findByBookId(String bookId);

    @Query(value = "SELECT AVG(rating) FROM reviews WHERE book_id = :bookId", nativeQuery = true)
    Double getAverageRatingByBookId(@Param("bookId") String bookId);
}
