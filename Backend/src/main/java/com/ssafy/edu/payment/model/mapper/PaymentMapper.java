package com.ssafy.edu.payment.model.mapper;

import com.ssafy.edu.payment.model.Payment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper {
    void insertPayment(Payment payment);
    
    Payment getPaymentByPaymentKey(String tossPaymentKey);
    
    void updatePaymentStatus(String tossPaymentKey, String status);
}
