package com.ssafy.edu.controller;

import com.ssafy.edu.user.model.dao.UserDao;
import com.ssafy.edu.user.model.dto.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "OAuth2 Controller", description = "소셜 로그인 관련 API")
@RestController
@RequestMapping("/login/oauth2")
@CrossOrigin("*")
public class OAuth2Controller {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2Controller.class);
    private static final String ERROR_PREFIX = "오류가 발생했습니다: ";

    private final UserDao userDao;

    public OAuth2Controller(UserDao userDao) {
        this.userDao = userDao;
    }

    private ResponseEntity<Map<String, Object>> createResponse(boolean success, String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }

    private ResponseEntity<Map<String, Object>> createResponse(boolean success, String message, Object data, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        response.put("data", data);
        return new ResponseEntity<>(response, status);
    }

    @Operation(summary = "OAuth2 로그인 성공", description = "소셜 로그인 성공 후 사용자 정보 반환")
    @GetMapping("/success")
    public ResponseEntity<Map<String, Object>> success(@AuthenticationPrincipal OAuth2User oauth2User) {
        logger.info("OAuth2 login success for user");
        try {
            String email = extractEmail(oauth2User);
            User user = userDao.findByUserEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Map<String, Object> userData = getUserData(user);
            return createResponse(true, "로그인에 성공했습니다.", userData, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid user in OAuth2 success: ", e);
            return createResponse(false, "유효하지 않은 사용자입니다: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error in OAuth2 success: ", e);
            return createResponse(false, ERROR_PREFIX + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "OAuth2 로그인 실패", description = "소셜 로그인 실패 시 에러 메시지 반환")
    @GetMapping("/failure")
    public ResponseEntity<Map<String, Object>> failure() {
        logger.error("OAuth2 login failure");
        return createResponse(false, "소셜 로그인에 실패했습니다.", HttpStatus.BAD_REQUEST);
    }

    private Map<String, Object> getUserData(User user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", user.getUserId());
        userData.put("email", user.getUserEmail());
        userData.put("name", user.getUserName());
        userData.put("profileImage", user.getPImage());
        userData.put("isLogin", user.getIsLogin());
        return userData;
    }

    private String extractEmail(OAuth2User oauth2User) {
        Map<String, Object> attributes = oauth2User.getAttributes();

        if (attributes.containsKey("email")) {
            return (String) attributes.get("email");
        } else if (attributes.containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            return (String) kakaoAccount.get("email");
        } else if (attributes.containsKey("response")) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("email");
        }
        throw new IllegalArgumentException("Could not extract email from OAuth2User");
    }


}