package com.ssafy.edu.controller;

import com.ssafy.edu.user.model.dto.User;
import com.ssafy.edu.user.model.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("*")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @Operation(summary = "전체 사용자 목록 조회", description = "모든 사용자 정보를 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> userList() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<User> users = userService.userList();
            response.put("success", true);
            response.put("users", users);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "사용자 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인 (필수: userEmail, password)")
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> userLogin(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            User loginUser = userService.userLogin(user);
            if (loginUser != null) {
                loginUser.setIsLogin(1);
                userService.updateLoginStatus(loginUser);
                
                response.put("success", true);
                response.put("message", "로그인에 성공했습니다.");
                response.put("user", loginUser);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("success", false);
                response.put("message", "이메일 또는 비밀번호가 일치하지 않습니다.");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "로그인 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "회원가입", description = "새로운 사용자 등록 (필수: userEmail, password, userName)")
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> userSignup(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            int result = userService.userSignup(user);
            if (result > 0) {
                response.put("success", true);
                response.put("message", "회원가입이 완료되었습니다.");
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                response.put("success", false);
                response.put("message", "회원가입에 실패했습니다.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "회원가입 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "회원정보 수정", description = "사용자 정보 수정 (필수: userId, 수정가능: userName, pImage)")
    @PatchMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> userUpdate(@PathVariable int userId, @RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            user.setUserId(userId);
            int result = userService.userUpdate(user);
            
            if (result > 0) {
                response.put("success", true);
                response.put("message", "회원정보가 수정되었습니다.");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("success", false);
                response.put("message", "회원정보 수정에 실패했습니다.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "회원정보 수정 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "이메일 중복체크", description = "이메일 중복 확인 (필수: userEmail)")
    @GetMapping("/check/email/{userEmail}")
    public ResponseEntity<Map<String, Object>> userIdCheck(@PathVariable String userEmail) {
        Map<String, Object> response = new HashMap<>();
        try {
            int result = userService.userIdCheck(userEmail);
            response.put("available", result == 0);
            response.put("message", result == 0 ? "사용 가능한 이메일입니다." : "이미 사용중인 이메일입니다.");
            return new ResponseEntity<>(response, result == 0 ? HttpStatus.OK : HttpStatus.CONFLICT);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "이메일 중복 확인 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "닉네임 중복체크", description = "닉네임 중복 확인 (필수: userName)")
    @GetMapping("/check/name/{userName}")
    public ResponseEntity<Map<String, Object>> userNameCheck(@PathVariable String userName) {
        Map<String, Object> response = new HashMap<>();
        try {
            int result = userService.userNameCheck(userName);
            response.put("available", result == 0);
            response.put("message", result == 0 ? "사용 가능한 닉네임입니다." : "이미 사용중인 닉네임입니다.");
            return new ResponseEntity<>(response, result == 0 ? HttpStatus.OK : HttpStatus.CONFLICT);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "닉네임 중복 확인 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "비밀번호 수정", description = "비밀번호 변경 (필수: userId, password)")
    @PatchMapping("/{userId}/password")
    public ResponseEntity<Map<String, Object>> updatePassword(
            @PathVariable int userId,
            @RequestBody Map<String, String> request) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            if (!request.containsKey("password")) {
                response.put("success", false);
                response.put("message", "비밀번호를 입력해주세요.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            
            User user = new User();
            user.setUserId(userId);
            user.setPassword(request.get("password"));
            
            int result = userService.updatePassword(user);
            
            response.put("success", result > 0);
            response.put("message", result > 0 ? "비밀번호가 성공적으로 수정되었습니다." : "비밀번호 수정에 실패했습니다.");
            return new ResponseEntity<>(response, result > 0 ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "비밀번호 수정 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "로그아웃", description = "사용자 로그아웃 처리 (필수: userId)")
    @PostMapping("/{userId}/logout")
    public ResponseEntity<Map<String, Object>> userLogout(@PathVariable int userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = new User();
            user.setUserId(userId);
            user.setIsLogin(0);
            
            int result = userService.updateLoginStatus(user);
            
            if (result > 0) {
                response.put("success", true);
                response.put("message", "로그아웃 되었습니다.");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("success", false);
                response.put("message", "로그아웃에 실패했습니다.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "로그아웃 처리 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}