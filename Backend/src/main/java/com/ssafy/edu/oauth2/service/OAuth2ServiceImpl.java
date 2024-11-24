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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuth2ServiceImpl extends DefaultOAuth2UserService implements OAuth2Service {

    private final UserDao userDao;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        return processOAuth2User(userRequest, oauth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String provider = userRequest.getClientRegistration().getRegistrationId();

        Map<String, Object> attributes = oauth2User.getAttributes();
        Map<String, Object> userInfo;

        switch (provider) {
            case "naver":
                userInfo = (Map<String, Object>) attributes.get("response");
                break;
            case "kakao":
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                userInfo = new HashMap<>();
                userInfo.put("email", kakaoAccount.get("email"));
                userInfo.put("nickname", profile.get("nickname"));
                userInfo.put("profile_image", profile.get("profile_image_url"));
                break;
            case "google":
                userInfo = attributes;
                break;
            default:
                throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + provider + " is not supported yet.");
        }

        String email = (String) userInfo.get(provider.equals("google") ? "email" : "email");
        String nickname = (String) userInfo.get(provider.equals("google") ? "name" : "nickname");
        String profileImage = (String) userInfo.get(provider.equals("google") ? "picture" :
                provider.equals("kakao") ? "profile_image_url" : "profile_image");

        Optional<User> userOptional = userDao.findByUserEmail(email);

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            updateExistingUser(existingUser, nickname, profileImage);
        } else {
            createNewUser(email, nickname, profileImage, provider);
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