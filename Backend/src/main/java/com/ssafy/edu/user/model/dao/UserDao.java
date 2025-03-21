package com.ssafy.edu.user.model.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import com.ssafy.edu.user.model.dto.User;

@Mapper
public interface UserDao {
    
    public User userLogin(User user);
    
    public int userSignup(User user);
    
    public int userUpdate(User user);
    
    public int userDelete(int userId);
    
    public User userDetail(int userId);
    
    public int userIdCheck(String userEmail);
    
    public int userNameCheck(String userName);
    
    public boolean checkPassword(User user);
    
    public int updatePassword(User user);
    
    public void updateLoginStatus(User user);
    
    public int checkLoginStatus(User user);
    
    public List<User> searchByEmail(Map<String, Object> params);
    
    public int getEmailSearchCount(Map<String, Object> params);
    
    public List<User> searchByName(Map<String, Object> params);
    
    public int getNameSearchCount(Map<String, Object> params);
    
    public List<User> searchByAdminStatus(Map<String, Object> params);
    
    public int getAdminSearchCount(Map<String, Object> params);
    
    public int updateProfileImage(int userId, String pImage);
    
    public List<User> searchEverything(Map<String, Object> params);
    
    public int getTotalEverythingCount(Map<String, Object> params);
    
    public List<User> userList();
    
    public int getTotalUserCount();
    
    public User confirmPassword(User user);
    
    
    public Optional<User> findByUserEmail(String email);
    
    public int updateOAuth2User(User user);
    
    public int saveOAuth2User(User user);

    public void deleteGoogleImage();
}

