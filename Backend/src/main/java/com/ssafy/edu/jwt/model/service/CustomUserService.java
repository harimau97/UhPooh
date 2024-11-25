package com.ssafy.edu.jwt.model.service;

import org.springframework.stereotype.Service;
import com.ssafy.edu.jwt.model.dao.tokenDao;
import com.ssafy.edu.jwt.model.util.JwtTokenUtil;

@Service
public class CustomUserService {
  
  private final tokenDao tDao;
  private final JwtTokenUtil jwtTokenUtil;
  
  public CustomUserService(tokenDao tDao, JwtTokenUtil jwtTokenUtil) {
    this.tDao = tDao;
    this.jwtTokenUtil = jwtTokenUtil;
  }
  
  // Login User and Generate Token
  public String loginUser(Long userId) {
    // 토큰 생성
    String token = jwtTokenUtil.generateToken(userId);
    
      // DB에 토큰 저장
      tDao.insertAccessToken(userId, token);
    
    return token;
  }
  
  // Validate Token
  public boolean validateAccessToken(String token) {
    if (!jwtTokenUtil.validateToken(token))
      return false;
    
    Long userId = jwtTokenUtil.getUserIdFromToken(token);
    String dbToken = tDao.getAccessTokenByUserId(userId);
    
    return token.equals(dbToken);
  }
  
  // Logout User
  public void logoutUser(int userId) {
    tDao.invalidateAccessToken((long) userId);
  }
  
  // Token Provider
  public String tokenProvider(int userId) {
    return tDao.getAccessTokenByUserId((long) userId);
  }
  
  // 토큰 비교
  public boolean getToken(String token, int userId) {
    String storedToken = tDao.getToken(token, (long) userId);
    if (storedToken != null) {
      System.out.println("Stored token: " + storedToken);
      return true;
    } else {
      return false;
    }
  }
}