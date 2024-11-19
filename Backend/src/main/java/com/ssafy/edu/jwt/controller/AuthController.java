package com.ssafy.edu.jwt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ssafy.edu.jwt.service.CustomUserService;



@RestController
@RequestMapping("/none")
public class AuthController {

  @Autowired
  private CustomUserService customUserService;

  public AuthController(CustomUserService customUserService) {
    super();
    this.customUserService = customUserService;
  }

  @GetMapping("/protected")
  public ResponseEntity<String> protectedEndpoint() {
    return ResponseEntity.ok("You have accessed a protected endpoint!");
  }

  @PostMapping("/login")
  public String login(@RequestParam Long userId) {
    return customUserService.loginUser(userId);
  }

  @GetMapping("/validate")
  public boolean validate(@RequestParam String token) {
    return customUserService.validateAccessToken(token);
  }

  @PostMapping("/logout")
  public String logout(@RequestParam Long userId) {
    customUserService.logoutUser(userId);
    return "Logged out successfully!";
  }
}
