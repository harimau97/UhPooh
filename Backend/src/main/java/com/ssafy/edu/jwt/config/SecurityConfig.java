package com.ssafy.edu.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf().disable() // CSRF 비활성화
        .authorizeHttpRequests(auth -> auth // 위 경로들은 인증 없이 접근 허용
            .anyRequest().permitAll() // 그 외 요청은 인증 필요
        );
    return http.build();
  }
}


// .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**",
// "/webjars/**", "api/auth/login", // 인증 없이 접근 가능한 엔드포인트 추가
// "/auth/register", "/api/user/signup")
