package com.example.bookstore_db.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private List<CartItemDTO> items;
    private Double totalAmount;

    @Data
    public static class CartItemDTO {
        private String googleBookId;
        private String title;
        private String thumbnail;
        private Double price;
        private Integer quantity;
    }
}
