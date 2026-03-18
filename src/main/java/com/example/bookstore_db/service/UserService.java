package com.example.bookstore_db.service;

public interface UserService {
    // Hàm xử lý đổi mật khẩu
    boolean updatePassword(String username, String oldPassword, String newPassword);
}
