package com.example.bookstore_db.controller;

import com.example.bookstore_db.dto.JwtResponse;
import com.example.bookstore_db.dto.LoginRequest;
import com.example.bookstore_db.dto.RegisterRequest;
import com.example.bookstore_db.entity.User;
import com.example.bookstore_db.repository.UserRepository;
import com.example.bookstore_db.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        //  Tìm user
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User không tồn tại!"));

        //  Kiểm tra mật khẩu
        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            // 3. Tạo Token
            String jwt = tokenProvider.generateToken(user.getUsername());
            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    user.getId(),
                    user.getUsername()
            ));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai mật khẩu rồi fen ơi!");
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());

        // Lưu vào DB
        userRepository.save(user);

        return ResponseEntity.ok("Đăng ký thành công!");
    }

}