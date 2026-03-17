package com.example.bookstore_db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class BookService {
    private final String GOOGLE_BOOKS_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    @Autowired
    private RestTemplate restTemplate;

    public Object searchBooks(String query) {
        String url = GOOGLE_BOOKS_URL + query;
        // Gọi sang Google và nhận kết quả
        return restTemplate.getForObject(url, Object.class);
    }

    public Double getPriceFromApi(Map<String, Object> saleInfo) {
        if (saleInfo == null || !"FOR_SALE".equals(saleInfo.get("saleability"))) {
            return 0.0; // Hoặc để null nếu sách không bán
        }

        Map<String, Object> retailPrice = (Map<String, Object>) saleInfo.get("retailPrice");
        if (retailPrice != null && retailPrice.containsKey("amount")) {
            // Chuyển đổi về Double cho chắc ăn
            return Double.valueOf(retailPrice.get("amount").toString());
        }
        return 0.0;
    }
}