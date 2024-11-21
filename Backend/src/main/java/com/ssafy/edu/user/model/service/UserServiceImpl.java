package com.ssafy.edu.user.model.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.ssafy.edu.user.model.dao.UserDao;
import com.ssafy.edu.user.model.dto.User;

@Service
public class UserServiceImpl implements UserService {

  private final UserDao userDao;

  public UserServiceImpl(UserDao userDao) {

    this.userDao = userDao;
  }


  @Override
  public List<User> userList() {

    return userDao.userList();
  }

  @Override
  public User userLogin(User user) {

    return userDao.userLogin(user);
  }

  @Override
  public int userSignup(User user) {

    return userDao.userSignup(user);
  }

  @Override
  public int userUpdate(User user) {

    return userDao.userUpdate(user);
  }

  @Override
  public int userDelete(int userId) {

    return userDao.userDelete(userId);
  }

  @Override
  public User userDetail(int userId) {

    return userDao.userDetail(userId);
  }

  @Override
  public int userIdCheck(String userEmail) {

    return userDao.userIdCheck(userEmail);
  }

  @Override
  public int userNameCheck(String userName) {

    return userDao.userNameCheck(userName);
  }

  @Override
  public boolean checkPassword(User user) {
    return userDao.checkPassword(user);
  }

  @Override
  public int updatePassword(User user) {
    return userDao.updatePassword(user);
  }

  @Override
  public void updateLoginStatus(User user) {

    userDao.updateLoginStatus(user);
  }

  @Override
  public List<User> searchByEmail(Map<String, Object> params) {
    return userDao.searchByEmail(params);
  }

  @Override
  public int getEmailSearchCount(Map<String, Object> params) {
    return userDao.getEmailSearchCount(params);
  }

  @Override
  public List<User> searchByName(Map<String, Object> params) {
    return userDao.searchByName(params);
  }

  @Override
  public int getNameSearchCount(Map<String, Object> params) {
    return userDao.getNameSearchCount(params);
  }

  @Override
  public List<User> searchByAdminStatus(Map<String, Object> params) {
    return userDao.searchByAdminStatus(params);
  }

  @Override
  public int getAdminSearchCount(Map<String, Object> params) {
    return userDao.getAdminSearchCount(params);
  }


  @Override
  public int checkLoginStatus(User user) {
    return userDao.checkLoginStatus(user);
  }
}
