package com.example.bookstore_db.repository;

import com.example.bookstore_db.entity.Order;
import com.example.bookstore_db.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderDetail, Long> {
    
}
