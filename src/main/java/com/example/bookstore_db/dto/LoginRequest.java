package com.example.bookstore_db.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
