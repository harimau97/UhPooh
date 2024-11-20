package com.ssafy.edu.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ssafy.edu.jwt.dao.tokenDao;
import com.ssafy.edu.jwt.util.JwtTokenUtil;

@Service
public class CustomUserService {

  @Autowired
  private tokenDao tdao;


  public CustomUserService(tokenDao tdao) {
    super();
    this.tdao = tdao;

  }

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  // Login User and Generate Token
  public String loginUser(Long userId) {
    String token = jwtTokenUtil.generateToken(userId);

    // Save Access Token in DB
    tdao.insertAccessToken(userId, token);

    return token;
  }

  // Validate Token
  public boolean validateAccessToken(String token) {
    if (!jwtTokenUtil.validateToken(token))
      return false;

    Long userId = jwtTokenUtil.getUserIdFromToken(token);
    String dbToken = tdao.getAccessTokenByUserId(userId);

    return token.equals(dbToken);
  }

  // Logout User
  public void logoutUser(int userId) {

    tdao.invalidateAccessToken((long) userId);

  }

  // token provider
  public String tokenProvider(int userId) {
    return tdao.getAccessTokenByUserId((long) userId);
  }

  // clear user's token to avoid error
  // public void clearToken(int userId) {
  // tdao.clearToken((long) userId);
  // }

  // 토큰 비교
  public boolean getToken(String token, int userId) {
    if (tdao.getToken(token, (long) userId) != null) {
      System.out.println(tdao.getToken(token, (long) userId));
      return true;
    } else {
      return false;
    }
  }
}
