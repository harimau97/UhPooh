package com.ssafy.edu.user.model.service;

import java.util.List;
import java.util.Map;

import com.ssafy.edu.user.model.dto.User;

public interface UserService {
    
    User userLogin(User user);
    
    int userSignup(User user);
    
    int userUpdate(User user);
    
    int userDelete(int userId);
    
    User userDetail(int userId);
    
    int userIdCheck(String userEmail);
    
    int userNameCheck(String userName);
    
    boolean checkPassword(User user);
    
    int updatePassword(User user);
    
    void updateLoginStatus(User user);
    
    int checkLoginStatus(User user);
    
    List<User> searchByEmail(Map<String, Object> params);
    
    int getEmailSearchCount(Map<String, Object> params);
    
    List<User> searchByName(Map<String, Object> params);
    
    int getNameSearchCount(Map<String, Object> params);
    
    List<User> searchByAdminStatus(Map<String, Object> params);
    
    int getAdminSearchCount(Map<String, Object> params);
    
    int updateProfileImage(int userId, String pImage);
    
    List<User> searchEverything(Map<String, Object> params);
    
    int getTotalEverythingCount(Map<String, Object> params);
    
    List<User> userList();
    
    int getTotalUserCount();
    
    User confirmPassword(User user);;
    
}
