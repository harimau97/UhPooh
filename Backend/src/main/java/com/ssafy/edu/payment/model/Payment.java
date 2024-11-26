package com.ssafy.edu.payment.model;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    private int paymentId;
    private int userId;
    private String tossPaymentKey;
    private int amount;
    private String status;
    private LocalDateTime regTime;
    private LocalDateTime updTime;
}
