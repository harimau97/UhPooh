package com.ssafy.edu.config;

import com.ssafy.edu.oauth2.service.OAuth2Service;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired
  private OAuth2Service oAuth2Service;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  @Order(1)  // OAuth2 필터체인 (최우선 적용)
  public SecurityFilterChain oauth2FilterChain(HttpSecurity http) throws Exception {
    http
            .securityMatcher("/oauth2/**", "/login/**")  // OAuth2 관련 URL만 처리
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/login/**", "/oauth2/**").permitAll()
                    .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                    .defaultSuccessUrl("/login/oauth2/success", true)
                    .userInfoEndpoint(userInfo -> userInfo
                            .userService(oAuth2Service)
                    )
                    .failureUrl("/login/oauth2/failure")
            );

    return http.build();
  }

  @Bean
  @Order(2)  // 이미지 처리 필터체인
  public SecurityFilterChain imageFilterChain(HttpSecurity http) throws Exception {
    http
            .securityMatcher("/image/**", "/api/image/**")  // 이미지 관련 URL만 처리
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/image/**", "/api/image/**").permitAll()
                    .anyRequest().authenticated()
            );

    return http.build();
  }

  @Bean
  @Order(3)  // 기본 필터체인 (마지막 적용)
  public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
    http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/",
                            "/css/**",
                            "/images/**",
                            "/js/**",
                            "/swagger-ui/**",
                            "/v3/api-docs/**",
                            "/swagger-resources/**",
                            "/webjars/**",
                            "/api/auth/login",
                            "/auth/register",
                            "/api/user/signup"
                    ).permitAll()
                    .anyRequest().permitAll()  // 기존 설정대로 모든 요청 허용
            );

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("*"));
    configuration.setAllowedMethods(Arrays.asList("*"));
    configuration.setAllowedHeaders(Arrays.asList("*"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public StandardServletMultipartResolver multipartResolver() {
    return new StandardServletMultipartResolver();
  }
}