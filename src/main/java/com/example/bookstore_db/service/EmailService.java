package com.example.bookstore_db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async // Chạy ngầm để không bắt User đợi
    public void sendOrderConfirmation(String toEmail, String fullName, Long orderId, Double totalAmount) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("bookstore@hust.edu.vn");
        message.setTo(toEmail);
        message.setSubject("Xác nhận đơn hàng #" + orderId);
        message.setText("Chào " + fullName + ",\n\n" +
                "Cảm ơn ông đã ủng hộ Bookstore! Đơn hàng của ông đã được ghi nhận thành công.\n" +
                "Mã đơn hàng: " + orderId + "\n" +
                "Tổng thanh toán: " + totalAmount + " VNĐ\n\n" +
                "Chúng tôi sẽ giao hàng sớm nhất có thể!");

        mailSender.send(message);
    }
}
