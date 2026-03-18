package com.example.bookstore_db.controller;


import com.example.bookstore_db.entity.User;
import com.example.bookstore_db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

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
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);
        }).orElse(ResponseEntity.notFound().build());
    }

//    // Thêm User mới
//    @PostMapping
//    public User createUser(@RequestBody User user) {
//        return userRepository.save(user);
//    }
}
