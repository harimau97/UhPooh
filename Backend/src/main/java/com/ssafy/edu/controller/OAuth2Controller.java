package com.ssafy.edu.controller;

import com.ssafy.edu.jwt.model.service.CustomUserService;
import com.ssafy.edu.user.model.dao.UserDao;
import com.ssafy.edu.user.model.dto.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/login/oauth2")
@CrossOrigin(origins = "${app.frontend-url}", allowCredentials = "true")
@Tag(name = "OAuth2 Controller", description = "소셜 로그인 관련 API")
@RequiredArgsConstructor
public class OAuth2Controller {
    
    private static final Logger logger = LoggerFactory.getLogger(OAuth2Controller.class);
    
    private final String frontendUrl = "http://localhost:5173";
    
    private final UserDao userDao;
    private final CustomUserService customUserService;
    
    @GetMapping("/success")
    public void success(HttpServletResponse response, @AuthenticationPrincipal OAuth2User oauth2User) throws IOException {
        logger.info("OAuth2 login success for user");
        try {
            String email = extractEmail(oauth2User);
            User user = userDao.findByUserEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            // 토큰 생성
            String token = customUserService.loginUser((long) user.getUserId());
            
            // 프론트엔드로 리다이렉트
            String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl)
                    .path("/oauth2/callback")
                    .queryParam("token", token)
                    .queryParam("userId", user.getUserId())
                    .queryParam("email", user.getUserEmail())
                    .queryParam("name", user.getUserName())
                    .build().toUriString();
            
            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            logger.error("Error in OAuth2 success: ", e);
            response.sendRedirect(frontendUrl + "/login?error=true");
        }
    }
    
    @GetMapping("/failure")
    public void failure(HttpServletResponse response) throws IOException {
        logger.error("OAuth2 login failure");
        response.sendRedirect(frontendUrl + "/login?error=true");
    }
    
    private String extractEmail(OAuth2User oauth2User) {
        Map<String, Object> attributes = oauth2User.getAttributes();
        
        if (attributes.containsKey("email")) {
            return (String) attributes.get("email");
        } else if (attributes.containsKey("response")) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("email");
        }
        
        throw new IllegalArgumentException("Could not extract email from OAuth2User");
    }
}