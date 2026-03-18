package com.example.bookstore_db.service;

import com.example.bookstore_db.entity.User;
import com.example.bookstore_db.repository.UserRepository;
import com.example.bookstore_db.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean updatePassword(String username, String oldPassword, String newPassword) {
        // Tìm User
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // KIỂM TRA MẬT KHẨU CŨ
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {

                // MÃ HÓA MẬT KHẨU MỚI và lưu lại
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return true;
            }
        }

        return false;
    }
}
