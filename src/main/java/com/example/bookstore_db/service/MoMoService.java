package com.example.bookstore_db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class MoMoService {

    @Value("${momo.partner-code}")
    private String partnerCode;

    @Value("${momo.access-key}")
    private String accessKey;

    @Value("${momo.secret-key}")
    private String secretKey;

    @Value("${momo.endpoint}")
    private String endpoint;

    @Value("${momo.redirect-base-url}")
    private String redirectBaseUrl;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Kết quả tạo thanh toán MoMo: payUrl (browser) + deeplink (app)
     */
    public static class MoMoResult {
        public final String payUrl;
        public final String deeplink;
        public MoMoResult(String payUrl, String deeplink) {
            this.payUrl = payUrl;
            this.deeplink = deeplink;
        }
    }

    /**
     * Tạo yêu cầu thanh toán MoMo cho đơn hàng.
     * Trả về MoMoResult chứa payUrl và deeplink.
     */
    public MoMoResult createPayment(Long orderId, double amount) {
        try {
            String requestId   = UUID.randomUUID().toString();
            // orderId MoMo = "{ourOrderId}-{timestamp}" để có thể parse lại
            String momoOrderId = orderId + "-" + System.currentTimeMillis();
            String orderInfo   = "Thanh toan don hang #" + orderId + " - BookStore";
            String redirectUrl = redirectBaseUrl + "/api/payment/momo-return";
            // ipnUrl cần public; trong dev dùng cùng host (MoMo sẽ thử gọi nhưng thất bại — không ảnh hưởng flow)
            String ipnUrl      = redirectBaseUrl + "/api/payment/momo-ipn";
            String requestType = "captureWallet";
            String extraData   = "";

            // Chuỗi ký HMAC-SHA256 (thứ tự alphabet theo tài liệu MoMo v2)
            String rawSignature =
                    "accessKey="   + accessKey   +
                    "&amount="     + (long) amount +
                    "&extraData="  + extraData   +
                    "&ipnUrl="     + ipnUrl      +
                    "&orderId="    + momoOrderId +
                    "&orderInfo="  + orderInfo   +
                    "&partnerCode="+ partnerCode +
                    "&redirectUrl="+ redirectUrl +
                    "&requestId="  + requestId   +
                    "&requestType="+ requestType;

            String signature = hmacSHA256(secretKey, rawSignature);

            // Request body
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("partnerCode", partnerCode);
            body.put("accessKey",   accessKey);
            body.put("requestId",   requestId);
            body.put("amount",      (long) amount);
            body.put("orderId",     momoOrderId);
            body.put("orderInfo",   orderInfo);
            body.put("redirectUrl", redirectUrl);
            body.put("ipnUrl",      ipnUrl);
            body.put("lang",        "vi");
            body.put("extraData",   extraData);
            body.put("requestType", requestType);
            body.put("signature",   signature);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, entity, Map.class);
            Map responseBody = response.getBody();

            if (responseBody == null) {
                throw new RuntimeException("MoMo trả về response rỗng");
            }

            int resultCode = ((Number) responseBody.getOrDefault("resultCode", -1)).intValue();
            if (resultCode != 0) {
                throw new RuntimeException("MoMo lỗi: " + responseBody.get("message"));
            }

            String payUrl  = (String) responseBody.get("payUrl");
            String deeplink = (String) responseBody.get("deeplink");
            return new MoMoResult(payUrl, deeplink);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo thanh toán MoMo: " + e.getMessage(), e);
        }
    }

    // ── Verify signature từ MoMo return/IPN ────────────────────────────────────
    public boolean verifyReturnSignature(
            String momoOrderId, String requestId, long amount,
            int resultCode, String message, String responseTime,
            String orderInfo, String payType, String transId,
            String receivedSignature) {
        try {
            String rawSig =
                    "accessKey="    + accessKey    +
                    "&amount="      + amount        +
                    "&message="     + message       +
                    "&orderId="     + momoOrderId   +
                    "&orderInfo="   + orderInfo     +
                    "&orderType="   + ""            +
                    "&partnerCode=" + partnerCode   +
                    "&payType="     + payType       +
                    "&requestId="   + requestId     +
                    "&responseTime="+ responseTime  +
                    "&resultCode="  + resultCode    +
                    "&transId="     + transId;
            String expected = hmacSHA256(secretKey, rawSig);
            return expected.equals(receivedSignature);
        } catch (Exception e) {
            return false;
        }
    }

    private String hmacSHA256(String key, String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec spec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmac.init(spec);
        byte[] hash = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}


