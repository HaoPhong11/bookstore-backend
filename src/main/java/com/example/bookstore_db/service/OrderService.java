package com.example.bookstore_db.service;

import com.example.bookstore_db.dto.OrderDetailRequest;
import com.example.bookstore_db.dto.OrderRequest;
import com.example.bookstore_db.entity.Order;
import com.example.bookstore_db.entity.OrderDetail;
import com.example.bookstore_db.entity.User;
import com.example.bookstore_db.repository.OrderRepository;
import com.example.bookstore_db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public Order createOrder(OrderRequest dto) {
        Order order = new Order();

        // Copy thông tin cơ bản từ DTO sang Entity
        order.setUserId(dto.getUserId());
        order.setSubTotal(dto.getSubTotal());
        order.setShippingFee(dto.getShippingFee());
        order.setDiscount(dto.getDiscount());
        order.setTotalPrice(dto.getTotalPrice());

        // Thông tin người nhận
        order.setReceiverName(dto.getReceiverName());
        order.setReceiverPhone(dto.getReceiverPhone());
        order.setReceiverEmail(dto.getReceiverEmail());
        order.setProvince(dto.getProvince());
        order.setDistrict(dto.getDistrict());
        order.setDetailedAddress(dto.getDetailedAddress());
        order.setNote(dto.getNote());

        // Trạng thái & Thanh toán
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setStatus("PENDING");
        order.setCreatedAt(new Date());

        // 4. Xử lý danh sách Sách (OrderDetail)
        List<OrderDetail> items = new ArrayList<>();
        if (dto.getItems() != null) {
            for (OrderDetailRequest itemDto : dto.getItems()) {
                OrderDetail item = new OrderDetail();
                item.setBookId(itemDto.getBookId());
                item.setQuantity(itemDto.getQuantity());
                item.setPrice(itemDto.getPrice());
                item.setBookTitle(itemDto.getBookTitle());
                // Truncate imgUrl tránh vượt quá VARCHAR(255) — Google Books URL có thể dài ~270+ ký tự
                String rawUrl = itemDto.getImgUrl();
                item.setImgUrl(rawUrl != null && rawUrl.length() > 255 ? null : rawUrl);
                item.setOrder(order);
                items.add(item);
            }
        }
        order.setOrderItems(items);

        // 5. Lưu vào Database
        return orderRepository.save(order);
    }
}