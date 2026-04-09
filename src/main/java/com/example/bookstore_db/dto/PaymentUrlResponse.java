package com.example.bookstore_db.dto;

/**
 * Response khi tạo đơn hàng.
 * COD: payUrl + deeplink = null
 * MOMO: payUrl và deeplink từ MoMo API
 */
public class PaymentUrlResponse {
    private Long orderId;
    private String payUrl;      // URL mở trên browser (luôn có nếu MOMO)
    private String deeplink;    // URL mở thẳng app MoMo (có thể null)
    private String paymentMethod;

    public PaymentUrlResponse(Long orderId, String payUrl, String deeplink, String paymentMethod) {
        this.orderId = orderId;
        this.payUrl = payUrl;
        this.deeplink = deeplink;
        this.paymentMethod = paymentMethod;
    }

    public Long getOrderId() { return orderId; }
    public String getPayUrl() { return payUrl; }
    public String getDeeplink() { return deeplink; }
    public String getPaymentMethod() { return paymentMethod; }
}

