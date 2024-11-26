package com.ssafy.edu.service;

import com.ssafy.edu.config.TossPaymentConfig;
import com.ssafy.edu.payment.model.PaymentRequest;
import com.ssafy.edu.payment.model.PaymentResponse;
import com.ssafy.edu.payment.model.Payment;
import com.ssafy.edu.payment.model.mapper.PaymentMapper;
import com.ssafy.edu.user.model.mapper.UserMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
public class TossPaymentService {

    private final RestTemplate restTemplate;
    private final TossPaymentConfig tossPaymentConfig;
    private final PaymentMapper paymentMapper;
    private final UserMapper userMapper;
    private static final String TOSS_PAYMENT_URL = "https://api.tosspayments.com/v1/payments";

    @Autowired
    public TossPaymentService(TossPaymentConfig tossPaymentConfig, PaymentMapper paymentMapper, UserMapper userMapper) {
        this.tossPaymentConfig = tossPaymentConfig;
        this.paymentMapper = paymentMapper;
        this.userMapper = userMapper;
        this.restTemplate = new RestTemplate();
    }

    public PaymentResponse requestPayment(PaymentRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(tossPaymentConfig.getSecretKey());

        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("amount", request.getAmount());
        payloadMap.put("orderId", request.getOrderId());
        payloadMap.put("orderName", request.getOrderName());
        payloadMap.put("successUrl", request.getSuccessUrl());
        payloadMap.put("failUrl", request.getFailUrl());
        payloadMap.put("customerEmail", request.getCustomerEmail());
        payloadMap.put("customerName", request.getCustomerName());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payloadMap, headers);
        
        return restTemplate.postForObject(TOSS_PAYMENT_URL, entity, PaymentResponse.class);
    }

    @Transactional
    public PaymentResponse confirmPayment(String paymentKey, String orderId, Long amount, String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(tossPaymentConfig.getSecretKey());

        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("paymentKey", paymentKey);
        payloadMap.put("orderId", orderId);
        payloadMap.put("amount", amount);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payloadMap, headers);
        
        PaymentResponse response = restTemplate.postForObject(TOSS_PAYMENT_URL + "/confirm", entity, PaymentResponse.class);

        // username으로 userId 조회
        int userId = userMapper.getUserIdByUsername(username);

        // DB에 결제 정보 저장
        Payment payment = Payment.builder()
                .userId(userId)
                .tossPaymentKey(paymentKey)
                .amount(amount.intValue())
                .status("DONE")
                .build();

        paymentMapper.insertPayment(payment);
        
        return response;
    }

    public PaymentResponse getPaymentInfo(String paymentKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(tossPaymentConfig.getSecretKey());
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        return restTemplate.getForObject(TOSS_PAYMENT_URL + "/" + paymentKey, PaymentResponse.class);
    }

    public Payment getPaymentByPaymentKey(String paymentKey) {
        return paymentMapper.getPaymentByPaymentKey(paymentKey);
    }

    public void updatePaymentStatus(String paymentKey, String status) {
        paymentMapper.updatePaymentStatus(paymentKey, status);
    }
}
