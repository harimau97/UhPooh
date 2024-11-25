package com.ssafy.edu.config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.UriComponentsBuilder;
import com.ssafy.edu.jwt.model.service.CustomUserService;
import com.ssafy.edu.oauth2.service.OAuth2Service;
import com.ssafy.edu.user.model.dao.UserDao;
import com.ssafy.edu.user.model.dto.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final OAuth2Service oAuth2Service;
  private final CustomUserService customUserService;
  private final UserDao userDao;
  private final OAuth2AuthorizedClientService authorizedClientService;

  public SecurityConfig(OAuth2Service oAuth2Service, CustomUserService customUserService,
                        UserDao userDao, OAuth2AuthorizedClientService authorizedClientService) {
    this.oAuth2Service = oAuth2Service;
    this.customUserService = customUserService;
    this.userDao = userDao;
    this.authorizedClientService = authorizedClientService;
  }

  @Bean
  public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
    return new HttpSessionOAuth2AuthorizationRequestRepository();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(
                    auth -> auth.requestMatchers("/uhpooh/api/user/login", "/oauth2/authorization/**",
                            "/login/oauth2/code/*", "/images/**").permitAll().anyRequest().permitAll())
            .formLogin(form -> form.disable()).httpBasic(basic -> basic.disable())
            .oauth2Login(oauth2 -> oauth2
                    .authorizationEndpoint(authorization -> authorization.baseUri("/oauth2/authorization")
                            .authorizationRequestRepository(authorizationRequestRepository()))
                    .redirectionEndpoint(redirection -> redirection.baseUri("/login/oauth2/code/*"))
                    .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2Service))
                    .successHandler((request, servletResponse, authentication) -> {
                      try {
                        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                        Map<String, Object> attributes = oauth2User.getAttributes();

                        String email;
                        String name;
                        String provider;

                        // 제공자별로 다른 응답 구조 처리
                        if (request.getRequestURI().contains("google")) {
                          // Google 응답 처리
                          email = (String) attributes.get("email");
                          name = (String) attributes.get("name");
                          provider = "google";
                        } else if (request.getRequestURI().contains("naver")) {
                          // Naver 응답 처리
                          Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                          email = (String) response.get("email");
                          name = (String) response.get("name");
                          provider = "naver";
                        } else if (request.getRequestURI().contains("github")) {
                          // GitHub 응답 처리
                          log.info("GitHub OAuth Response - Full Attributes: {}", attributes);

                          OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                          OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                                  oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());

                          String token = client.getAccessToken().getTokenValue();
                          String emailsUrl = "https://api.github.com/user/emails";

                          RestTemplate restTemplate = new RestTemplate();
                          HttpHeaders headers = new HttpHeaders();
                          headers.setBearerAuth(token);
                          HttpEntity<?> entity = new HttpEntity<>(headers);

                          email = null;
                          try {
                            ResponseEntity<List> response =
                                    restTemplate.exchange(emailsUrl, HttpMethod.GET, entity, List.class);

                            List<Map<String, Object>> emails = response.getBody();
                            log.info("GitHub Emails Response: {}", emails);

                            if (emails != null && !emails.isEmpty()) {
                              for (Map<String, Object> emailObj : emails) {
                                if (Boolean.TRUE.equals(emailObj.get("primary"))) {
                                  email = (String) emailObj.get("email");
                                  break;
                                }
                              }
                              if (email == null && !emails.isEmpty()) {
                                email = (String) ((Map<String, Object>) emails.get(0)).get("email");
                              }
                            }
                          } catch (Exception e) {
                            log.error("GitHub 이메일 가져오기 실패", e);
                          }

                          // 이메일을 가져오지 못한 경우 대체 이메일 생성
                          if (email == null) {
                            String login = (String) attributes.get("login");
                            email = login + "@github.com";
                            log.info("Created fallback email: {}", email);
                          }

                          name = (String) attributes.get("login");
                          if (name == null) {
                            name = (String) attributes.get("name");
                          }
                          provider = "github";

                          log.info("GitHub User Info - Email: {}, Name: {}", email, name);
                        } else {
                          throw new RuntimeException("Unsupported OAuth2 provider");
                        }

                        log.info("Provider: {}", provider);
                        log.info("Email: {}", email);
                        log.info("Name: {}", name);

                        Optional<User> userOptional = userDao.findByUserEmail(email);
                        User user;

                        if (userOptional.isPresent()) {
                          user = userOptional.get();
                          user.setIsLogin(1);
                          userDao.updateOAuth2User(user);
                        } else {
                          user = new User();
                          user.setUserEmail(email);
                          user.setUserName(name);
                          user.setProvider(provider);
                          user.setIsLogin(1);
                          userDao.saveOAuth2User(user);
                        }

                        String token = customUserService.loginUser((long) user.getUserId());

                        String redirectUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:5173")
                                .path("/oauth2/callback").queryParam("token", token)
                                .queryParam("userId", user.getUserId()).queryParam("email", user.getUserEmail())
                                .queryParam("name", user.getUserName()).build().encode().toUriString();

                        servletResponse.sendRedirect(redirectUrl);
                      } catch (Exception e) {
                        log.error("OAuth2 Login Error", e);
                        servletResponse.sendRedirect("http://localhost:5173/login?error=true");
                      }
                    }))
            .exceptionHandling(
                    handling -> handling.authenticationEntryPoint((request, response, authException) -> {
                      // 인증되지 않은 요청에 대해 401 상태 코드 반환
                      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                      response.setContentType("application/json");
                      response.getWriter().write("{\"error\": \"Unauthorized\"}");
                    }));

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}