package com.ssafy.edu.jwt.model.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class filterConfig {

  @Bean
  public FilterRegistrationBean<jwtFilter> jwtFilterRegistration() {
    FilterRegistrationBean<jwtFilter> registrationBean = new FilterRegistrationBean<>();

    // jwtFilter 등록
    registrationBean.setFilter(new jwtFilter());

    // 필터 적용 경로 설정
    registrationBean.addUrlPatterns("/uhpooh/api/*");

    // 필터 순서 설정 (Spring Security 이후로 설정 가능)
    registrationBean.setOrder(Integer.MAX_VALUE);

    return registrationBean;
  }
}
