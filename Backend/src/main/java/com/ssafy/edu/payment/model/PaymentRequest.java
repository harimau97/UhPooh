package com.ssafy.edu.payment.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private Long amount;
    private String orderId;
    private String orderName;
    private String successUrl;
    private String failUrl;
    private String customerEmail;
    private String customerName;
}
