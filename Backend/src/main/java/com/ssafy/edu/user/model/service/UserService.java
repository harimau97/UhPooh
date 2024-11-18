package com.ssafy.edu.user.model.service;

import com.ssafy.edu.user.model.dto.User;

import java.util.List;

public interface UserService {
    
    List<User> userList();
    
    User userLogin(User user);
    
    int userSignup(User user);
    
    int userUpdate(User user);
    
    int userDelete(int userId);
    
    User userDetail(int userId);
    
    int userIdCheck(String userEmail);
    
    int userNameCheck(String userName);
    
    int updatePassword(User user);
    
    int updateLoginStatus(User user);
    
}
