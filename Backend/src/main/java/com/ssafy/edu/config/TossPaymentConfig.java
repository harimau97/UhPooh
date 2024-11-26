package com.ssafy.edu.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application-oauth.properties")
public class TossPaymentConfig {
    @Value("${tosspayments.secret-key}")
    private String secretKey;

    @Value("${tosspayments.client-key}")
    private String clientKey;

    public String getSecretKey() {
        return secretKey;
    }

    public String getClientKey() {
        return clientKey;
    }
}
