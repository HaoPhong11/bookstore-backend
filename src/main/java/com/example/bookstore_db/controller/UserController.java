package com.example.bookstore_db.controller;


import com.example.bookstore_db.dto.ChangePasswordRequest;
import com.example.bookstore_db.dto.UserRequest;
import com.example.bookstore_db.dto.UserResponse;
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
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Lấy danh sách tất cả User
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    // Cập nhật thông tin User
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserRequest userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setFullName(userDetails.getFullName());
            user.setPhoneNumber(userDetails.getPhoneNumber());
            user.setAddress(userDetails.getAddress());
            user.setEmail(userDetails.getEmail());
            user.setProvince(userDetails.getProvince());
            user.setDistrict(userDetails.getDistrict());
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(toResponse(updatedUser));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Helper: chuyển User entity → UserResponse
    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getAddress(),
                user.getProvince(),
                user.getDistrict()
        );
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (request.getOldPassword() == null || request.getOldPassword().isBlank() ||
                request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Vui lòng nhập đầy đủ mật khẩu cũ và mới!");
        }

        if (request.getNewPassword().equals(request.getOldPassword())) {
            return ResponseEntity.badRequest().body("Mật khẩu mới phải khác mật khẩu cũ!");
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu có khớp nhau không
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Mật khẩu mới không khớp!");
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
