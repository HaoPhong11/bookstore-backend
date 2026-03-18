package com.example.bookstore_db.controller;


import com.example.bookstore_db.dto.ChangePasswordRequest;
import com.example.bookstore_db.entity.User;
import com.example.bookstore_db.repository.UserRepository;
import com.example.bookstore_db.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    // Lấy thông tin 1 User
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Lấy danh sách tất cả User
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Cập nhật thông tin User
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setFullName(userDetails.getFullName());
            user.setPhoneNumber(userDetails.getPhoneNumber());
            user.setAddress(userDetails.getAddress());
            user.setEmail(userDetails.getEmail());
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu có khớp nhau không
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Mật khẩu mới không khớp!");
        }

        if (request.getNewPassword().equals(request.getOldPassword())) {
            return ResponseEntity.badRequest().body("Mật khẩu mới phải khác mật khẩu cũ!");
        }

        // Gọi Service để kiểm tra mật khẩu cũ và lưu mật khẩu mới (đã mã hóa BCrypt)
        boolean success = userService.updatePassword(username, request.getOldPassword(), request.getNewPassword());

        if (success) {
            return ResponseEntity.ok("Đổi mật khẩu thành công!");
        } else {
            return ResponseEntity.badRequest().body("Mật khẩu cũ không chính xác!");
        }
    }

}
