package com.example.bookstore_db.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long id;
    public JwtResponse(String accessToken, Long id) {
        this.accessToken = accessToken;
        this.id = id;
    }
}
