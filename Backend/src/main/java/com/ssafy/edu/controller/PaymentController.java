package com.ssafy.edu.controller;

import com.ssafy.edu.payment.model.Payment;
import com.ssafy.edu.payment.model.PaymentRequest;
import com.ssafy.edu.payment.model.PaymentResponse;
import com.ssafy.edu.service.TossPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin("*")
public class PaymentController {
    
    private final TossPaymentService tossPaymentService;
    
    @Autowired
    public PaymentController(TossPaymentService tossPaymentService) {
        this.tossPaymentService = tossPaymentService;
    }
    
    @PostMapping("/request")
    public ResponseEntity<PaymentResponse> requestPayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = tossPaymentService.requestPayment(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponse> confirmPayment(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount,
            Principal principal) {
        // principal.getName()은 현재 로그인한 사용자의 아이디(username)를 반환합니다
        String username = principal.getName();
        
        PaymentResponse response = tossPaymentService.confirmPayment(paymentKey, orderId, amount, username);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{paymentKey}")
    public ResponseEntity<PaymentResponse> getPaymentInfo(@PathVariable String paymentKey) {
        PaymentResponse response = tossPaymentService.getPaymentInfo(paymentKey);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/history/{paymentKey}")
    public ResponseEntity<Payment> getPaymentHistory(@PathVariable String paymentKey) {
        Payment payment = tossPaymentService.getPaymentByPaymentKey(paymentKey);
        return ResponseEntity.ok(payment);
    }
}
