package com.ssafy.edu.oauth2.service;

import com.ssafy.edu.oauth2.OAuth2AuthenticationProcessingException;
import com.ssafy.edu.user.model.dao.UserDao;
import com.ssafy.edu.user.model.dto.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuth2ServiceImpl extends DefaultOAuth2UserService implements OAuth2Service {
    
    private final UserDao userDao;
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        return processOAuth2User(userRequest, oauth2User);
    }
    
    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String provider = userRequest.getClientRegistration().getRegistrationId();
        
        Map<String, Object> attributes = oauth2User.getAttributes();
        Map<String, Object> userInfo;
        
        // 디버깅을 위한 로그 추가
        log.info("OAuth2 Provider: {}", provider);
        log.info("OAuth2 Attributes: {}", attributes);
        
        switch (provider) {
            case "naver":
                userInfo = (Map<String, Object>) attributes.get("response");
                break;
            case "github":
                // GitHub는 attributes가 곧 userInfo이며, 디버깅을 위한 로그 추가
                log.info("GitHub OAuth Attributes: {}", attributes);
                
                userInfo = new HashMap<>();
                String email = (String) attributes.get("email");
                
                // GitHub API를 통해 이메일 가져오기 시도
                if (email == null || email.isEmpty()) {
                    try {
                        String token = userRequest.getAccessToken().getTokenValue();
                        String emailsUrl = "https://api.github.com/user/emails";
                        
                        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                        headers.setBearerAuth(token);
                        org.springframework.http.HttpEntity<?> entity = new org.springframework.http.HttpEntity<>(headers);
                        
                        org.springframework.http.ResponseEntity<List> response = restTemplate.exchange(
                                emailsUrl,
                                org.springframework.http.HttpMethod.GET,
                                entity,
                                List.class
                        );
                        
                        List<Map<String, Object>> emails = response.getBody();
                        if (emails != null && !emails.isEmpty()) {
                            // primary 이메일 찾기
                            for (Map<String, Object> emailObj : emails) {
                                if (Boolean.TRUE.equals(emailObj.get("primary"))) {
                                    email = (String) emailObj.get("email");
                                    break;
                                }
                            }
                            // primary가 없으면 첫 번째 이메일 사용
                            if (email == null && !emails.isEmpty()) {
                                email = (String) ((Map<String, Object>) emails.get(0)).get("email");
                            }
                        }
                    } catch (Exception e) {
                        log.error("GitHub 이메일 가져오기 실패", e);
                    }
                }
                
                String name = (String) attributes.get("login");
                if (name == null) {
                    name = (String) attributes.get("name");
                }
                
                userInfo.put("email", email);
                userInfo.put("nickname", name);
                userInfo.put("profile_image", attributes.get("avatar_url"));
                
                log.info("Processed GitHub userInfo: {}", userInfo);
                break;
            case "google":
                userInfo = attributes;
                break;
            default:
                throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + provider + " is not supported yet.");
        }
        
        String userEmail = (String) userInfo.get(provider.equals("google") ? "email" : "email");
        String nickname = (String) userInfo.get(provider.equals("google") ? "name" : "nickname");
        String profileImage = (String) userInfo.get(provider.equals("google") ? "picture" :
                provider.equals("github") ? "profile_image" : "profile_image");
        
        log.info("Extracted Email: {}", userEmail);
        log.info("Extracted Nickname: {}", nickname);
        log.info("Extracted Profile Image: {}", profileImage);
        
        Optional<User> userOptional = userDao.findByUserEmail(userEmail);
        
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            updateExistingUser(existingUser, nickname, profileImage);
        } else {
            createNewUser(userEmail, nickname, profileImage, provider);
        }
        
        return oauth2User;
    }
    
    private void updateExistingUser(User existingUser, String nickname, String profileImage) {
        existingUser.setUserName(nickname);
        existingUser.setPImage(profileImage);
        existingUser.setIsLogin(1);
        userDao.updateOAuth2User(existingUser);
    }
    
    private void createNewUser(String email, String nickname, String profileImage, String provider) {
        User user = new User();
        user.setUserEmail(email);
        user.setUserName(nickname);
        user.setPImage(profileImage);
        user.setProvider(provider);
        user.setIsLogin(1);
        userDao.saveOAuth2User(user);
    }
}