package com.ssafy.edu.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {

  private int userId;
  private String userEmail;
  private String password;
  private String userName;
  private String userAddress;
  private String pImage;
  private int isAdmin;
  private String regTime;
  private String updTime;
  private int isLogin;
  
}
