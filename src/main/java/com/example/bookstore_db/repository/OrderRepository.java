package com.example.bookstore_db.repository;

import com.example.bookstore_db.entity.Order;
import com.example.bookstore_db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

}
