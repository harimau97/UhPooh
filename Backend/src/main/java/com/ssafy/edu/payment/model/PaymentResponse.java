package com.ssafy.edu.payment.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponse {
    private String paymentKey;
    private String orderId;
    private Long amount;
    private String status;
    private String requestedAt;
    private String approvedAt;
}
