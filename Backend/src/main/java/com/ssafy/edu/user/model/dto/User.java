package com.ssafy.edu.user.model.dto;

import lombok.*;

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
    private String pImage;
    private int isAdmin;
    private String regTime;
    private String updTime;
    private int isLogin;
}
