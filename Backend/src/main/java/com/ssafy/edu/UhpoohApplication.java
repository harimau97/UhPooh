package com.ssafy.edu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = {
    "com.ssafy.edu.payment.model.mapper",
    "com.ssafy.edu.user.model.mapper"
})
public class UhpoohApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(UhpoohApplication.class, args);
    }
    
}
