package com.example.bookstore_db.controller;

import com.example.bookstore_db.entity.Order;
import com.example.bookstore_db.repository.OrderRepository;
import com.example.bookstore_db.service.MoMoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired private OrderRepository orderRepository;
    @Autowired private MoMoService moMoService;

    /**
     * MoMo redirect người dùng về đây sau khi thanh toán (browser redirect, KHÔNG phải server-to-server).
     * Vì request đến từ browser trên điện thoại (cùng mạng WiFi) nên local IP hoạt động bình thường.
     */
    @GetMapping("/momo-return")
    public void momoReturn(
            @RequestParam(value = "orderId",      required = false) String momoOrderId,
            @RequestParam(value = "resultCode",   required = false, defaultValue = "-1") int resultCode,
            @RequestParam(value = "requestId",    required = false, defaultValue = "") String requestId,
            @RequestParam(value = "amount",       required = false, defaultValue = "0") long amount,
            @RequestParam(value = "message",      required = false, defaultValue = "") String message,
            @RequestParam(value = "orderInfo",    required = false, defaultValue = "") String orderInfo,
            @RequestParam(value = "orderType",    required = false, defaultValue = "") String orderType,
            @RequestParam(value = "transId",      required = false, defaultValue = "0") long transId,
            @RequestParam(value = "payType",      required = false, defaultValue = "") String payType,
            @RequestParam(value = "responseTime", required = false, defaultValue = "") String responseTime,
            @RequestParam(value = "extraData",    required = false, defaultValue = "") String extraData,
            @RequestParam(value = "signature",    required = false, defaultValue = "") String signature,
            HttpServletResponse response
    ) throws IOException {

        boolean success = resultCode == 0;
        Long ourOrderId = null;

        if (momoOrderId != null && !momoOrderId.isEmpty()) {
            try {
                // Format: "{ourOrderId}-{timestamp}"
                ourOrderId = Long.parseLong(momoOrderId.split("-")[0]);
            } catch (NumberFormatException ignored) {}
        }

        if (success && ourOrderId != null) {
            Optional<Order> orderOpt = orderRepository.findById(ourOrderId);
            orderOpt.ifPresent(order -> {
                if ("PENDING".equals(order.getStatus())) {
                    order.setStatus("PROCESSING");
                    orderRepository.save(order);
                }
            });
        }

        // Redirect về app qua deep link
        String deepLink = "bookstore://payment/result?success=" + success
                + "&orderId=" + (ourOrderId != null ? ourOrderId : "");
        response.sendRedirect(deepLink);
    }

    /**
     * IPN endpoint (server-to-server từ MoMo). Trong local dev MoMo không gọi được vào đây,
     * nhưng vẫn cần khai báo để MoMo không báo lỗi khi tạo payment.
     */
    @PostMapping("/momo-ipn")
    public ResponseEntity<Map<String, String>> momoIpn(@RequestBody Map<String, Object> body) {
        try {
            String momoOrderId = (String) body.getOrDefault("orderId", "");
            int rc = ((Number) body.getOrDefault("resultCode", -1)).intValue();
            boolean success = rc == 0;

            if (success && !momoOrderId.isEmpty()) {
                Long ourOrderId = Long.parseLong(momoOrderId.split("-")[0]);
                orderRepository.findById(ourOrderId).ifPresent(order -> {
                    if ("PENDING".equals(order.getStatus())) {
                        order.setStatus("PROCESSING");
                        orderRepository.save(order);
                    }
                });
            }
        } catch (Exception ignored) {}
        return ResponseEntity.ok(Map.of("status", "OK"));
    }
}

