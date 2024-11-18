package com.ssafy.edu.user.model.dao;

import com.ssafy.edu.user.model.dto.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao {
    
    public List<User> userList();
    
    public User userLogin(User user);
    
    public int userSignup(User user);
    
    public int userUpdate(User user);
    
    public int userDelete(int userId);
    
    public User userDetail(int userId);
    
    public int userIdCheck(String userEmail);
    
    public int userNameCheck(String userName);
    
    public int updatePassword(User user);
    
    public int updateLoginStatus(User user);
}
